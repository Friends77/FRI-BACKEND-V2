package com.friends.security.securityException

import com.friends.common.exception.ErrorCode

open class AuthenticationException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class InvalidTokenException : AuthenticationException(ErrorCode.INVALID_TOKEN)

class InvalidAccessTokenException : AuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN)

class InvalidRefreshTokenException : AuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN)

class MissingSocialAccessTokenException : AuthenticationException(ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN)

class MissingRefreshTokenException : AuthenticationException(ErrorCode.MISSING_REFRESH_TOKEN)

class EmailNotFoundException : AuthenticationException(ErrorCode.EMAIL_NOT_FOUND)

class EmailDuplicateException : AuthenticationException(ErrorCode.EMAIL_ALREADY_EXISTS)

class InvalidPasswordException : AuthenticationException(ErrorCode.INVALID_PASSWORD)

class DuplicateNewPasswordException : AuthenticationException(ErrorCode.DUPLICATE_NEW_PASSWORD)

class InvalidNicknameException : AuthenticationException(ErrorCode.INVALID_NICKNAME)

class OAuth2ResetPasswordException : AuthenticationException(ErrorCode.OAUTH2_RESET_PASSWORD)
