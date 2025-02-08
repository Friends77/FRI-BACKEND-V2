package com.friends.security

import com.friends.member.entity.OAuth2Provider
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import java.time.LocalDate
import java.util.Date

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class LoginResponseDto(
    val memberId: Long,
    val accessToken: String,
    val refreshTokenExpiration: Date,
)

data class RegisterRequestDto(
    val authToken: String,
    val email: String? = null,
    val password: String? = null,
    val nickname: String,
    val birth: LocalDate,
    val gender: GenderEnum,
    val selfDescription: String? = null,
    val mbti: MbtiEnum? = null,
    val interestTag: List<Long> = emptyList(),
    val location: LocationDto? = null,
)

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
)

data class AtRtDto(
    val accessToken: String,
    val refreshToken: String,
)

data class OAuth2LoginDto(
    val isRegistered: Boolean,
    val memberId: Long? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val email: String? = null,
    val nickname: String? = null,
    val imageUrl: String? = null,
    val authToken: String? = null,
)

data class OAuth2LoginRequestDto(
    val code: String,
    val provider: OAuth2Provider,
)

data class OAuth2LoginResponseDto(
    val isRegistered: Boolean,
    val memberId: Long? = null,
    val accessToken: String? = null,
    val email: String? = null,
    val nickname: String? = null,
    val imageUrl: String? = null,
    val authToken: String? = null,
    val refreshTokenExpiration: Date? = null,
)

data class RefreshResponseDto(
    val accessToken: String,
    val refreshTokenExpiration: Date,
)

data class LogoutRequestDto(
    val accessToken: String?,
)

data class CheckNicknameResponseDto(
    val isValid: Boolean,
    val message: String,
)

data class CheckEmailResponseDto(
    val isValid: Boolean,
    val message: String,
)

data class PasswordResetRequestDto(
    val emailAuthToken: String,
    val newPassword: String,
)

data class ResetPasswordRequestDto(
    val emailAuthToken: String,
    val newPassword: String,
)
