package com.friends.email

import com.friends.common.exception.ErrorCode

abstract class EmailException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class EmailVerifyFailedException : EmailException(ErrorCode.INVALID_EMAIL_VERIFY_CODE)

class EmailSendFailedException : EmailException(ErrorCode.SMTP_CONNECTION_FAILED)
