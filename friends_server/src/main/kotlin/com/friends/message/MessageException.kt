package com.friends.message

import com.friends.common.exception.ErrorCode

abstract class MessageException(
    val errorCode: ErrorCode,
    cause: Throwable? = null,
) : RuntimeException(errorCode.errorMessage, cause)

class MessageNotFoundException : MessageException(ErrorCode.MESSAGE_NOT_FOUND)

class NotMessageSenderException : MessageException(ErrorCode.NOT_MESSAGE_SENDER)
