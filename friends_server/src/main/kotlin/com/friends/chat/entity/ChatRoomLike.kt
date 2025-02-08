package com.friends.chat.entity

import com.friends.common.entity.BaseTimeEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(indexes = [Index(name = "chat_room_like_chat_room_id_member_id_index", columnList = "chat_room_id, member_id")], uniqueConstraints = [UniqueConstraint(columnNames = ["chat_room_id", "member_id"], name = "chat_room_like_chat_room_id_member_id_unique")])
class ChatRoomLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_like_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", updatable = false, nullable = false)
    val chatRoom: ChatRoom,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    val member: Member,
) : BaseTimeEntity() {
    companion object {
        fun of(
            chatRoom: ChatRoom,
            member: Member,
        ): ChatRoomLike = ChatRoomLike(0L, chatRoom, member)
    }
}
