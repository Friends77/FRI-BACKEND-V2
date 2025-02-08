package com.friends.chat.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import com.friends.message.entity.Message
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

@Entity
@Table(
    name = "chat_room_member",
    indexes = [Index(name = "chat_room_member_member_id", columnList = "member_id"), Index(name = "chat_room_member_chat_Room_id", columnList = "chat_room_id")],
    uniqueConstraints = [jakarta.persistence.UniqueConstraint(columnNames = ["chat_room_id", "member_id"], name = "chat_room_member_unique")],
)
class ChatRoomMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", updatable = false, nullable = false)
    val chatRoom: ChatRoom,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    var lastReadMessage: Message,
) : BaseModifiableEntity() {
    companion object {
        fun of(
            chatRoom: ChatRoom,
            member: Member,
            lastReadMessage: Message,
        ): ChatRoomMember =
            ChatRoomMember(
                chatRoom = chatRoom,
                member = member,
                lastReadMessage = lastReadMessage,
            )
    }
}
