package com.friends.email

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class EmailVerifyController(
    private val emailVerifyService: EmailVerifyService,
) : EmailVerifyControllerSpec {
    @PostMapping("/send-verification-code")
    override fun sendVerifyEmail(
        @RequestBody emailDto: EmailDto,
    ): ResponseEntity<String> {
        emailVerifyService.sendVerifyEmail(emailDto.email)
        return ResponseEntity.ok("이메일로 인증 코드를 전송했습니다.")
    }

    @PostMapping("/verify-email")
    override fun verifyEmail(
        @RequestBody emailVerifyDto: EmailVerifyRequestDto,
    ): ResponseEntity<EmailVerifyResponseDto> {
        val token = emailVerifyService.verifyEmail(emailVerifyDto.email, emailVerifyDto.code)
        return ResponseEntity.ok(EmailVerifyResponseDto(token))
    }
}
