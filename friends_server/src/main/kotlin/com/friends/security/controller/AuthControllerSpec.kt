package com.friends.security.controller

import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.security.CheckEmailResponseDto
import com.friends.security.CheckNicknameResponseDto
import com.friends.security.LoginRequestDto
import com.friends.security.LoginResponseDto
import com.friends.security.LogoutRequestDto
import com.friends.security.OAuth2LoginRequestDto
import com.friends.security.OAuth2LoginResponseDto
import com.friends.security.PasswordResetRequestDto
import com.friends.security.RefreshResponseDto
import com.friends.security.RegisterRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Auth")
interface AuthControllerSpec {
    @Operation(
        description =
            "회원가입 API <br>" +
                "이메일 인증 api 를 통해 발행된 authToken 이 필요합니다.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "회원가입 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_TOKEN,
            ErrorCode.EMAIL_ALREADY_EXISTS,
            ErrorCode.INVALID_NICKNAME,
            ErrorCode.INVALID_PASSWORD,
            ErrorCode.NOT_FOUND_CATEGORY,
        ],
    )
    fun register(
        registerRequestDto: RegisterRequestDto,
        profileImage: MultipartFile?,
    ): ResponseEntity<String>

    @Operation(
        description =
            "이메일/패스워드 로그인 API <br>" +
                "쿠키에 refresh token 을 담아서 전달합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
            ),
        ],
    )
    fun login(
        loginRequestDto: LoginRequestDto,
    ): ResponseEntity<LoginResponseDto>

    @Operation(
        description =
            "OAuth2 로그인 API <br>" +
                "쿠키에 refresh token 을 담아서 전달합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.EMAIL_ALREADY_EXISTS,
        ],
    )
    fun oauth2Login(
        oauth2LoginRequestDto: OAuth2LoginRequestDto,
    ): ResponseEntity<OAuth2LoginResponseDto>

    @Operation(
        description = "refresh token 을 이용한 access token 과 refresh token 재발급 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "refresh 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_REFRESH_TOKEN,
        ],
    )
    fun refresh(
        refreshToken: String,
    ): ResponseEntity<RefreshResponseDto>

    @Operation(
        description =
            "로그아웃 API <br>" +
                "access token 과 refresh token 을 모두 만료시킵니다. (at, rt 가 없다면 아무것도 하지 않습니다.)" +
                "refresh token 을 쿠키에서 삭제시킵니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공",
            ),
        ],
    )
    fun logout(
        logoutRequestDto: LogoutRequestDto,
        refreshToken: String?,
    ): ResponseEntity<String>

    @Operation(
        description = "비밀번호 초기화 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "비밀번호 초기화 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_TOKEN,
            ErrorCode.EMAIL_NOT_FOUND,
        ],
    )
    fun resetPassword(
        passwordResetResponse: PasswordResetRequestDto,
    ): ResponseEntity<String>

    @Operation(
        description =
            "닉네임 유효성 검사 API <br>" +
                "이미 사용 중인 닉네임인지, 한글, 숫자, 영문 포함 2~20자인지 확인합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "닉네임 중복 확인 성공",
            ),
        ],
    )
    fun checkNickname(
        nickname: String,
    ): ResponseEntity<CheckNicknameResponseDto>

    @Operation(
        description =
            "이메일 유효성 검사 API <br>" +
                "이미 사용 중인 이메일인지 확인합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "이메일 중복 확인 성공",
            ),
        ],
    )
    fun checkEmail(
        email: String,
    ): ResponseEntity<CheckEmailResponseDto>
}
