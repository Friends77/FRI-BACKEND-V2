package com.friends.security.service

import com.friends.member.service.MemberService
import com.friends.security.ResetPasswordRequestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthPasswordService(
    private val memberService: MemberService,
    private val authValidator: AuthValidator,
) {
    @Transactional
    fun resetPassword(
        resetPasswordRequestDto: ResetPasswordRequestDto,
    ) {
        // emailAuthToken 검증하고 이메일 추출
        val email = authValidator.validateEmailAuthTokenAndReturnEmail(resetPasswordRequestDto.emailAuthToken)
        // 유저 존재 여부 확인 후 조회
        val member = memberService.getMemberByEmail(email)
        // 패스워드 변경 유효성 검사
        authValidator.validateResetPassword(member, resetPasswordRequestDto.newPassword)
        // 패스워드 변경
        member.updatePassword(resetPasswordRequestDto.newPassword)
    }
}
