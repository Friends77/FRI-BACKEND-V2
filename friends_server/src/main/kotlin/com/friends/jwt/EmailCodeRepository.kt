package com.friends.jwt

import com.friends.config.AuthProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class EmailCodeRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val authProperties: AuthProperties,
) {
    fun save(
        email: String,
        code: String,
    ) {
        redisTemplate.opsForValue().set(getCodeKey(email), code, authProperties.emailCodeExpiration, TimeUnit.SECONDS)
    }

    private fun getCodeKey(email: String) = "emailCode:$email"

    fun getCode(email: String): String? = redisTemplate.opsForValue().get(getCodeKey(email))
}
