package com.friends.email

data class EmailDto(
    // 이메일 유효성 검사 진행 미구현
    val email: String,
)

data class EmailVerifyRequestDto(
    val email: String,
    val code: String,
)

data class EmailVerifyResponseDto(
    val emailAuthToken: String,
)
