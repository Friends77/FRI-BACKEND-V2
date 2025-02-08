package com.friends.common.swagger

import com.friends.common.exception.ErrorCode

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorCodeExamples(val value: Array<ErrorCode>)
