package com.friends.secondaryToken

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class SecondaryTokenRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private fun getKey(token: String) = "secondaryToken:$token"

    fun save(
        token: String,
        memberId: Long,
        expiration: Long,
    ) {
        redisTemplate.opsForValue().set(getKey(token), memberId.toString(), expiration, TimeUnit.SECONDS)
    }

    fun getMemberId(token: String): Long? = redisTemplate.opsForValue().get(getKey(token))?.toLong()

    fun delete(token: String) = redisTemplate.delete(getKey(token))
}
