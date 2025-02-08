package com.friends.oauth2

/**
 * 각각의 OAuth2 에 맞게 데이터를 추출하는 인터페이스
 * (구글, 네이버 등)
 */
interface UserProfileExtractor {
    fun extract(attributes: Map<String, Any>): UserProfileDto
}

/**
 * 구글 OAuth2 에 맞게 데이터를 추출하는 클래스
 */
class GoogleUserProfileExtractor : UserProfileExtractor {
    override fun extract(attributes: Map<String, Any>): UserProfileDto {
        val name = attributes["name"] as String
        val email = attributes["email"] as String
        val imageUrl = attributes["picture"] as String
        return UserProfileDto(name, email, imageUrl)
    }
}

/**
 * 네이버 OAuth2 에 맞게 데이터를 추출하는 클래스
 */
class NaverUserProfileExtractor : UserProfileExtractor {
    override fun extract(attributes: Map<String, Any>): UserProfileDto {
        val response = attributes["response"] as Map<String, Any>
        val name = response["name"] as String
        val email = response["email"] as String
        val imageUrl = response["profile_image"] as String
        return UserProfileDto(name, email, imageUrl)
    }
}
