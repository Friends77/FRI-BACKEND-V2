package com.friends.oauth2

import com.friends.member.entity.OAuth2Provider
import org.springframework.stereotype.Service

@Service
class OAuth2Service(
    private val oAuth2Fetcher: OAuth2Fetcher,
) {
    fun getUserProfile(
        code: String,
        oAuth2Provider: OAuth2Provider,
    ): UserProfileDto {
        val accessTokenResponseDto = oAuth2Fetcher.getAccessToken(code, oAuth2Provider)
        val attributes = oAuth2Fetcher.getUserAttributes(accessTokenResponseDto.accessToken, oAuth2Provider)
        return oAuth2Provider.extract(attributes)
    }
}
