package com.friends.member.service

import com.friends.member.repository.MemberRepository
import com.friends.security.securityException.EmailNotFoundException
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun getMemberByEmail(email: String) = memberRepository.findByEmail(email) ?: throw EmailNotFoundException()
}
