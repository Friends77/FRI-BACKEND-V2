package com.friends.chat.entity

import com.friends.category.entity.Category
import com.friends.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "chat_room_category", uniqueConstraints = [UniqueConstraint(columnNames = ["chat_room_id", "category_id"], name = "chat_room_category_unique")])
class ChatRoomCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_category_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false, updatable = false)
    val chatRoom: ChatRoom,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, updatable = false)
    val category: Category,
) : BaseTimeEntity() {
    companion object {
        fun of(
            chatRoom: ChatRoom,
            category: Category,
        ): ChatRoomCategory = ChatRoomCategory(0L, chatRoom, category)
    }
}
