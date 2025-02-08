package com.friends.alarm

import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import java.time.LocalDateTime

data class AlarmResponseDto(
    val id: Long,
    val type: AlarmType,
    val message: String,
    val senderId: Long,
    val receiverId: Long,
    val invitedChatRoomId: Long? = null,
    val createdAt: LocalDateTime,
)

fun toAlarmResponseDto(alarm: Alarm) =
    AlarmResponseDto(
        id = alarm.id,
        type = alarm.getType(),
        message = alarm.message,
        senderId = alarm.sender.id,
        receiverId = alarm.receiver.id,
        invitedChatRoomId = alarm.invitedChatRoom?.id,
        createdAt = alarm.createdAt,
    )
