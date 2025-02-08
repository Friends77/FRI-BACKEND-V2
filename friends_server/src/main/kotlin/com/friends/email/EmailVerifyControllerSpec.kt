package com.friends.email

import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "EmailVerify", description = "이메일 인증 API")
interface EmailVerifyControllerSpec {
    @Operation(
        description = "이메일 인증 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "인증 코드 전송 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = String::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.SMTP_CONNECTION_FAILED,
        ],
    )
    fun sendVerifyEmail(
        @RequestBody
        emailDto: EmailDto,
    ): ResponseEntity<String>

    //TODO: 작성해야함
    fun verifyEmail(
        @RequestBody emailVerifyDto: EmailVerifyRequestDto,
    ): ResponseEntity<EmailVerifyResponseDto>
}
