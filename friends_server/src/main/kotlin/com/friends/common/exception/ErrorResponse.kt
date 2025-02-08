package com.friends.common.exception

class ErrorResponse private constructor(
    val code: Int,
    val errorMessage: String,
) {
    companion object {
        fun of(
            errorCode: ErrorCode,
            errorMessage: String?,
        ) = ErrorResponse(errorCode.code, errorMessage ?: errorCode.errorMessage)
    }

    fun toJson() =
        """{
        "code": $code,
        "errorMessage": "$errorMessage"
    }"""
}
