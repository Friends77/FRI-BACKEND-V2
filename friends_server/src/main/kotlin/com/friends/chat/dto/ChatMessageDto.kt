package com.friends.chat.dto

import com.friends.common.util.LocalDateTimeUtil
import com.friends.message.entity.MessageType
import java.time.LocalDateTime

/**
 * 채팅 웹소켓에서 메세지 전송은 로그인된 유저만 이용할 수 있기 때문에 senderId는 SecurityContextHolder에서 가져옵니다.
 */
data class ChatReceiveMessageDto(
    val clientMessageId: String,
    val chatRoomId: Long,
    val content: String,
    val type: MessageType,
)

data class ChatSendMessageDto(
    val clientMessageId: String? = null,
    val code: Int = 200,
    val messageId: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val content: String,
    val createdAt: Long,
    val type: MessageType,
) {
    constructor(
        clientMessageId: String? = null,
        messageId: Long,
        chatRoomId: Long,
        senderId: Long,
        content: String,
        createdAt: LocalDateTime,
        type: MessageType,
    ) : this(
        clientMessageId = clientMessageId,
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = senderId,
        content = content,
        createdAt = LocalDateTimeUtil.toTimeStamp(createdAt),
        type = type,
    )
}

data class ChatErrorMessageDto(
    val clientMessageId: String?,
    val code: Int,
    val message: String,
)

enum class PingPongType {
    PING,
    PONG,
}

data class PingPongDto(
    val type: String,
)
