package com.friends.jwt

import com.friends.config.AuthProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class AuthJwtRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val authProperties: AuthProperties,
) {
    private fun getAccessTokenKey(accessToken: String) = "accessToken:$accessToken"

    private fun getRefreshTokenKey(refreshToken: String) = "refreshToken:$refreshToken"

    /**
     * accessToken과 refreshToken을 저장합니다.
     * accessToken은 accessTokenExpiration 시간 동안, refreshToken은 refreshTokenExpiration 시간 동안 저장되고
     * 시간이 지나면 자동으로 삭제됩니다.
     */
    fun save(
        accessToken: String,
        refreshToken: String,
    ) {
        redisTemplate.opsForValue().set(
            getAccessTokenKey(accessToken),
            refreshToken,
            authProperties.accessTokenExpiration,
            TimeUnit.SECONDS,
        )
        redisTemplate.opsForValue().set(
            getRefreshTokenKey(refreshToken),
            accessToken,
            authProperties.refreshTokenExpiration,
            TimeUnit.SECONDS,
        )
    }

    fun getAccessToken(refreshToken: String): String? = redisTemplate.opsForValue().get(getRefreshTokenKey(refreshToken))

    fun getRefreshToken(accessToken: String): String? = redisTemplate.opsForValue().get(getAccessTokenKey(accessToken))

    fun deleteAccessToken(accessToken: String) = redisTemplate.delete(getAccessTokenKey(accessToken))

    fun deleteRefreshToken(refreshToken: String) = redisTemplate.delete(getRefreshTokenKey(refreshToken))
}
