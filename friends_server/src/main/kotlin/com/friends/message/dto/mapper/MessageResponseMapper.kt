package com.friends.message.dto.mapper

import com.friends.message.dto.MessageResponseDto
import com.friends.message.entity.Message

fun toMessageResponseDto(
    message: Message,
): MessageResponseDto =
    MessageResponseDto(
        messageId = message.id,
        senderId = message.sender.id,
        content = message.content,
        type = message.type,
        createdAt = message.createdAt,
    )
