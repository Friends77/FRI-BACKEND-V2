package com.friends.common.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component

@Component
class AopForTransaction {
    fun proceed(joinPoint: ProceedingJoinPoint): Any? = joinPoint.proceed() // joinPoint.proceed()는 AOP 대상 메서드를 실행
}
