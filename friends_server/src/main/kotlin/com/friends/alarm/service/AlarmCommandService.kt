package com.friends.alarm.service

import com.friends.alarm.AlarmNotFoundException
import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import com.friends.alarm.repository.AlarmRepository
import com.friends.alarm.toAlarmResponseDto
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.PingPongDto
import com.friends.chat.dto.PingPongType
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.repository.PingPongRepository
import com.friends.common.util.JsonUtil
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
@Transactional
class AlarmCommandService(
    private val memberRepository: MemberRepository,
    private val alarmRepository: AlarmRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val pingPongRepository: PingPongRepository,
) {
    private val onlineUserSessions = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()

    fun addOnlineUserSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        onlineUserSessions.computeIfAbsent(memberId) { ConcurrentHashMap.newKeySet() }.add(session)
    }

    fun removeOnlineUserSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        onlineUserSessions[memberId]?.removeIf {
            it.id == session.id
        }
    }

    @Scheduled(fixedRate = 30000) // 30초 마다 실행
    fun ping() {
        for (entry in onlineUserSessions) {
            val userSessions = entry.value
            userSessions.forEach { session ->
                try {
                    /**
                     * ping 이 존재한다는 것은 pong 을 받지 못했다는 것을 의미합니다.
                     * 이 경우에는 세션을 제거합니다.
                     */
                    if (pingPongRepository.existPing(session.id)) {
                        session.close()
                        userSessions.remove(session)
                    } else {
                        pingPongRepository.savePing(session.id)
                        session.sendMessage(TextMessage(JsonUtil.toJson(PingPongDto(PingPongType.PING.name.lowercase()))))
                    }
                } catch (e: Exception) {
                    // 에러가 발생할 경우 세션을 제거합니다.
                    session.close()
                    userSessions.remove(session)
                }
            }
        }
    }

    fun sendFriendRequestAlarm(
        requesterId: Long,
        receiverId: Long,
    ) {
        val requester = memberRepository.findById(requesterId).orElseThrow { MemberNotFoundException() }
        val receiver = memberRepository.findById(receiverId).orElseThrow { MemberNotFoundException() }
        val alarm =
            Alarm(
                sender = requester,
                receiver = receiver,
                type = AlarmType.FRIEND_REQUEST,
                message = "${requester.nickname}님이 친구 요청을 보냈습니다.",
            )

        sendAlarm(alarm)
    }

    fun sendChatInvitationAlarm(
        senderId: Long,
        receiverId: Long,
        chatRoomId: Long,
    ) {
        val sender = memberRepository.findById(senderId).orElseThrow { MemberNotFoundException() }
        val receiver = memberRepository.findById(receiverId).orElseThrow { MemberNotFoundException() }
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val alarm =
            Alarm(
                sender = sender,
                receiver = receiver,
                type = AlarmType.CHAT_ROOM_INVITATION,
                message = "${sender.nickname}님이 채팅방[${chatRoom.title}]에 초대를 보냈습니다.",
                invitedChatRoom = chatRoom,
            )

        sendAlarm(alarm)
    }

    private fun sendAlarm(
        alarm: Alarm,
    ) {
        alarmRepository.save(alarm)
        val alarmResponseDto = toAlarmResponseDto(alarm)
        onlineUserSessions[alarm.receiver.id]?.forEach {
            it.sendMessage(TextMessage(JsonUtil.toJson(alarmResponseDto)))
        }
    }

    fun changeAlarmType(
        alarmId: Long,
        alarmType: AlarmType,
    ) {
        val alarm = alarmRepository.findById(alarmId).orElseThrow { AlarmNotFoundException() }
        alarm.changeType(alarmType)
    }
}
