package com.friends.security.authentication

import com.friends.jwt.AtRtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthenticationCreator(
    private val atRtService: AtRtService,
) {
    fun createByAccessToken(accessToken: String): Authentication {
        val memberId = atRtService.getMemberId(accessToken)
        val authorities = atRtService.getAuthorities(accessToken)
        return UsernamePasswordAuthenticationToken(memberId, accessToken, authorities)
    }
}
