package com.friends.oauth2

import com.friends.common.exception.ErrorCode

abstract class OAuth2Exception(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class OAuth2NullResponseException : OAuth2Exception(ErrorCode.OAUTH2_NULL_RESPONSE)

class OAuth2AccessTokenFetchFailedException : OAuth2Exception(ErrorCode.OAUTH2_ACCESS_TOKEN_FETCH_FAILED)

class OAuth2UserInfoFetchFailedException : OAuth2Exception(ErrorCode.OAUTH2_USER_INFO_FETCH_FAILED)
