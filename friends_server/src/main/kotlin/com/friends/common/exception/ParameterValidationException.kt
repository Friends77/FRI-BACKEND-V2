package com.friends.common.exception

class ParameterValidationException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)
