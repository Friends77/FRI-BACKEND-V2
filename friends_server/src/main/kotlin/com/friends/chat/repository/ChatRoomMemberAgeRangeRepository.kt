package com.friends.chat.repository

import com.friends.chat.entity.AgeRangeEnum
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class ChatRoomMemberAgeRangeRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private fun getKey(chatRoomId: Long) = "chatRoom:$chatRoomId:mostAgeRange"

    fun save(
        chatRoomId: Long,
        ageRange: String,
    ) {
        val expireAt = TimeUnit.DAYS.toMillis(1) + TimeUnit.MINUTES.toMillis(30)
        redisTemplate.opsForValue().set(getKey(chatRoomId), ageRange, expireAt, TimeUnit.MILLISECONDS)
    }

    fun getMostAgeRange(chatRoomId: Long): AgeRangeEnum {
        val ageRange = redisTemplate.opsForValue().get(getKey(chatRoomId))?.toInt() ?: return AgeRangeEnum.UNKNOWN
        return AgeRangeEnum.entries.find { it.value == ageRange } ?: AgeRangeEnum.UNKNOWN
    }
}
