package com.friends.chat.util

import com.friends.chat.dto.ChatRoomInfoResponseDto

class SliceChatRoomInfoResponseDto(
    val content: List<ChatRoomInfoResponseDto>,
    val hasNext: Boolean,
)
