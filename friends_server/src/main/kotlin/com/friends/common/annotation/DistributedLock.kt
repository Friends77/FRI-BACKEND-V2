package com.friends.common.annotation

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val lockName: String,
    val identifier: String,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
    val waitTime: Long = 20L,
    val leaseTime: Long = 10L,
)
