package com.friends.chat.repository

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomCategoryRepository : JpaRepository<ChatRoomCategory, Long> {
    fun deleteByChatRoom(chatRoom: ChatRoom)
}
