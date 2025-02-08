package com.friends.alarm.entity

import com.friends.chat.entity.ChatRoom
import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Alarm(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    val sender: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    val receiver: Member,
    @Enumerated(EnumType.STRING)
    private var type: AlarmType,
    val message: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    val invitedChatRoom: ChatRoom? = null,
) : BaseModifiableEntity() {
    fun changeType(type: AlarmType) {
        this.type = type
    }

    fun getType(): AlarmType = type
}
