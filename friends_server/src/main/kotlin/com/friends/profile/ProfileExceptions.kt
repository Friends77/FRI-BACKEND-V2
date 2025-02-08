package com.friends.profile

import com.friends.common.exception.ErrorCode

abstract class ProfileExceptions(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class ProfileNullResponseException : ProfileExceptions(ErrorCode.PROFILE_NOT_FOUND)

class ProfileLocationNullException : ProfileExceptions(ErrorCode.PROFILE_LOCATION_NULL)
