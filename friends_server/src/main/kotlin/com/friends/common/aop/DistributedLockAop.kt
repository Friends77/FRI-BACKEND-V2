package com.friends.common.aop

import com.friends.common.annotation.DistributedLock
import com.friends.common.exception.InvalidRedisLockIdentifierException
import com.friends.common.exception.RedisLockFailedException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionTimedOutException

@Aspect
@Component
class DistributedLockAop(
    private val redissonClient: RedissonClient,
    private val aopForTransaction: AopForTransaction,
) {
    val log: Logger = LoggerFactory.getLogger(DistributedLockAop::class.java)

    @Around("@annotation(com.friends.common.annotation.DistributedLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any? { // joinPoint는 AOP 대상 메서드를 나타냄
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLock = method.getAnnotation(DistributedLock::class.java)
        val dynamicKey = getDynamicKeyFromMethodArgs(signature.parameterNames, joinPoint.args, distributedLock.identifier)

        val key = "${distributedLock.lockName}:$dynamicKey"

        val rLock = redissonClient.getLock(key)

        try {
            val available =
                rLock.tryLock(
                    distributedLock.waitTime,
                    distributedLock.leaseTime,
                    distributedLock.timeUnit,
                ) // 레디스 tryLock 호출 시도
            if (!available) {
                throw RedisLockFailedException()
            }
            return aopForTransaction.proceed(joinPoint)
        } catch (e: InterruptedException) {
            // tryLock 호출 중에 인터럽트가 발생한 경우
            log.error("Interrupted Exception: ${e.message}")
            throw RedisLockFailedException()
        } catch (e: TransactionTimedOutException) {
            // tryLock 호출 중에 트랜잭션 시간 초과가 발생한 경우
            log.error("Transaction Timed Out Exception: ${e.message}")
            throw RedisLockFailedException()
        } finally {
            try {
                rLock.unlock()
            } catch (e: IllegalMonitorStateException) {
                // unlock 호출 시 이미 잠금이 해제된 경우
                log.error("Redisson Lock already unlocked:  ${method.name} $key")
            }
        }
    }

    private fun getDynamicKeyFromMethodArgs(
        methodParameterNames: Array<String>,
        args: Array<Any>,
        identifier: String,
    ): String {
        for (i in methodParameterNames.indices) {
            if (methodParameterNames[i] == identifier) {
                val arg = args[i]
                return try {
                    val getIdMethod = arg::class.java.getMethod("getId") // 해당 식별자 필드의 getter 메서드를 호출하여 값을 가져옴
                    getIdMethod.invoke(arg).toString() // 문자열로 변환하여 반환
                } catch (e: NoSuchMethodException) {
                    arg.toString()
                }
            }
        }
        throw InvalidRedisLockIdentifierException()
    }
}
