package com.friends.chat.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class PingPongRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun savePing(webSocketSessionId: String) {
        redisTemplate.opsForValue().set(webSocketSessionId, "ping")
    }

    fun deletePing(webSocketSessionId: String) {
        redisTemplate.delete(webSocketSessionId)
    }

    fun existPing(webSocketSessionId: String): Boolean = redisTemplate.hasKey(webSocketSessionId)
}
