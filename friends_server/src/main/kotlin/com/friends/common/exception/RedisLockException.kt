package com.friends.common.exception

abstract class RedisLockException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class RedisLockFailedException : RedisLockException(ErrorCode.REDISSON_LOCK_FAILED)

class InvalidRedisLockIdentifierException : RedisLockException(ErrorCode.INVALID_REDISSON_IDENTIFIER)
