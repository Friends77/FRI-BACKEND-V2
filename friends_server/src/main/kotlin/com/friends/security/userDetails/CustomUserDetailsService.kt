package com.friends.security.userDetails

import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.security.securityException.EmailNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val member: Member = memberRepository.findByEmail(email) ?: throw EmailNotFoundException()
        return CustomUserDetails(member)
    }
}
