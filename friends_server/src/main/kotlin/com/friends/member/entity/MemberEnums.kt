package com.friends.member.entity

import com.friends.oauth2.GoogleUserProfileExtractor
import com.friends.oauth2.NaverUserProfileExtractor
import com.friends.oauth2.UserProfileDto
import com.friends.oauth2.UserProfileExtractor

enum class OAuth2Provider(
    private val extractor: UserProfileExtractor,
) {
    GOOGLE(GoogleUserProfileExtractor()),
    NAVER(NaverUserProfileExtractor()),
    ;

    fun extract(attributes: Map<String, Any>): UserProfileDto = extractor.extract(attributes)
}

enum class Role {
    ROLE_USER,
    ROLE_ADMIN,
}
