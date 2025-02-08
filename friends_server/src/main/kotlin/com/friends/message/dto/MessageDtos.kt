package com.friends.message.dto

import com.friends.common.util.LocalDateTimeUtil
import com.friends.message.entity.MessageType
import java.time.LocalDateTime

data class MessageResponseDto(
    val messageId: Long,
    val senderId: Long,
    val content: String,
    val type: MessageType,
    val createdAt: Long,
) {
    constructor(
        messageId: Long,
        senderId: Long,
        content: String,
        type: MessageType,
        createdAt: LocalDateTime,
    ) : this(messageId, senderId, content, type, LocalDateTimeUtil.toTimeStamp(createdAt))
}
