package com.friends.message.entity

import com.friends.chat.entity.ChatRoom
import com.friends.common.entity.BaseTimeEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false, updatable = false)
    val chatRoom: ChatRoom,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, updatable = false)
    val sender: Member,
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,
    @Column(nullable = false)
    var type: MessageType,
) : BaseTimeEntity() {
    companion object {
        fun of(
            chatRoom: ChatRoom,
            sender: Member,
            content: String,
            type: MessageType,
        ): Message = Message(0L, chatRoom, sender, content, type)

        fun enterMessage(
            nickname: String,
        ): String = "$nickname 님이 입장하셨습니다."

        fun exitMessage(
            nickname: String,
        ): String = "$nickname 님이 퇴장하셨습니다."

        fun changeManagerMessage(
            nickname: String,
        ): String = "$nickname 님이 새로운 방장으로 임명되었습니다."

        fun forceExitMessage(
            nickname: String,
        ): String = "$nickname 님이 강제 퇴장되었습니다."
    }
}

enum class MessageType {
    TEXT, // 일반 텍스트 메시지 및 이모지
    IMAGE,
    DELETE_MESSAGE,

    SYSTEM,
    SYSTEM_MEMBER_ENTER, // 채팅방 멤버 입장
    SYSTEM_MEMBER_LEAVE, // 채팅방 멤버 퇴장
    SYSTEM_NEW_MANAGER,
    ;

    companion object {
        fun getNonSystemTypes(): List<MessageType> = listOf(TEXT, IMAGE, DELETE_MESSAGE)

        fun getSystemTypes(): List<MessageType> = listOf(SYSTEM, SYSTEM_MEMBER_ENTER, SYSTEM_MEMBER_LEAVE, SYSTEM_NEW_MANAGER)
    }
}
