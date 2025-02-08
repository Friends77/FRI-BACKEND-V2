package com.friends.email

import com.friends.config.AuthProperties
import com.friends.jwt.EmailCodeRepository
import com.friends.jwt.JwtService
import com.friends.jwt.JwtType
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

const val EMAIL_VERIFY_TEMPLATE_PATH = "email-verify-template"
const val EMAIL_VERIFY_SUBJECT = "[Friends] 이메일 인증 코드를 확인해주세요"

@Service
class EmailVerifyService(
    private val emailService: EmailService,
    private val emailCodeRepository: EmailCodeRepository,
    private val templateEngine: TemplateEngine,
    private val authProperties: AuthProperties,
    private val jwtService: JwtService,
) {
    fun sendVerifyEmail(to: String) {
        val code = createVerifyCode()
        emailCodeRepository.save(to, code)
        val html = createEmailHtml(code)
        emailService.sendHtml(to, EMAIL_VERIFY_SUBJECT, html)
    }

    fun verifyEmail(
        email: String,
        code: String,
    ): String {
        if (!verifyCode(email, code)) {
            throw EmailVerifyFailedException()
        }
        return jwtService.createToken("email" to email, "type" to JwtType.EMAIL, expirationSeconds = authProperties.emailJwtExpiration)
    }

    private fun verifyCode(
        email: String,
        code: String,
    ): Boolean {
        val savedCode = emailCodeRepository.getCode(email) ?: return false
        return savedCode == code
    }

    private fun createEmailHtml(code: String): String {
        val context = Context()
        context.setVariable("code", code)
        return templateEngine.process(EMAIL_VERIFY_TEMPLATE_PATH, context)
    }

    private fun createVerifyCode(): String = (100000..999999).random().toString()
}
