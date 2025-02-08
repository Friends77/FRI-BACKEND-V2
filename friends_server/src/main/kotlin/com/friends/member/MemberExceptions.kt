package com.friends.member

import com.friends.common.exception.ErrorCode

abstract class MemberExceptions(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class MemberNotFoundException : MemberExceptions(ErrorCode.NOT_FOUND_MEMBER)
