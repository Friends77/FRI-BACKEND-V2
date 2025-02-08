package com.friends.security.service

import com.friends.jwt.JwtService
import com.friends.member.entity.Member
import com.friends.security.securityException.DuplicateNewPasswordException
import com.friends.security.securityException.InvalidPasswordException
import com.friends.security.securityException.InvalidTokenException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthValidator(
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
) {
    fun validateResetPassword(
        member: Member,
        newPassword: String,
    ) {
        if (!isValidPasswordPattern(newPassword)) {
            throw InvalidPasswordException()
        }

        if (isPreviousPassword(member.getPassword()!!, newPassword)) {
            throw DuplicateNewPasswordException()
        }
    }

    fun validateEmailAuthTokenAndReturnEmail(emailAuthToken: String): String {
        if (!jwtService.validate(emailAuthToken)) {
            throw InvalidTokenException()
        }
        return jwtService.getClaim(emailAuthToken, "email", String::class.java) ?: throw InvalidTokenException()
    }

    private fun isValidPasswordPattern(password: String): Boolean {
        val lengthRegex = Regex(".{8,20}") // 길이 제한
        val lowerCaseRegex = Regex(".*[a-z].*") // 소문자 포함
        val digitRegex = Regex(".*[0-9].*") // 숫자 포함
        val specialCharRegex = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*") // 특수문자 포함
        val noWhiteSpaceRegex = Regex("^[^\\s]*\$") // 공백 금지

        return lengthRegex.matches(password) &&
            lowerCaseRegex.containsMatchIn(password) &&
            digitRegex.containsMatchIn(password) &&
            specialCharRegex.containsMatchIn(password) &&
            noWhiteSpaceRegex.matches(password)
    }

    private fun isPreviousPassword(
        passwordInDB: String,
        newPassword: String,
    ): Boolean = passwordEncoder.matches(newPassword, passwordInDB)
}
