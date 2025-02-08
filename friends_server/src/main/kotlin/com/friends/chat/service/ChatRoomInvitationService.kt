package com.friends.chat.service

import com.friends.alarm.AlarmNotFoundException
import com.friends.alarm.entity.AlarmType
import com.friends.alarm.repository.AlarmRepository
import com.friends.alarm.service.AlarmCommandService
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.NotChatRoomMemberException
import com.friends.chat.dto.ChatRoomInvitationHandlerDto
import com.friends.chat.dto.ChatRoomInvitationRequestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChatRoomInvitationService(
    private val chatRoomCommandService: ChatRoomCommandService,
    private val chatRoomQueryService: ChatRoomQueryService,
    private val alarmCommandService: AlarmCommandService,
    private val alarmRepository: AlarmRepository,
) {
    @Transactional
    fun requestInvitation(
        requesterId: Long,
        chatRoomInvitationRequestDto: ChatRoomInvitationRequestDto,
    ) {
        val receiverIds = chatRoomInvitationRequestDto.receiverIdList
        val chatRoomId = chatRoomInvitationRequestDto.chatRoomId

        // 초대자가 채팅방에 속해있지 않으면 예외 발생
        if (!chatRoomQueryService.isUserInChatRoom(chatRoomId, requesterId)) {
            throw NotChatRoomMemberException()
        }

        receiverIds.map { receiverId ->
            // 수신자가 채팅방에 속해 있지 않으면 알람 전송
            if (!chatRoomQueryService.isUserInChatRoom(chatRoomId, receiverId)) {
                alarmCommandService.sendChatInvitationAlarm(
                    requesterId,
                    receiverId,
                    chatRoomId,
                )
            }
        }
    }

    @Transactional
    fun acceptInvitation(
        memberId: Long,
        chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ) {
        val alarmId = chatRoomInvitationHandlerDto.alarmId
        val alarm = alarmRepository.findById(alarmId).orElseThrow { AlarmNotFoundException() }
        // 알람 타입을 수락으로 변경
        alarmCommandService.changeAlarmType(alarmId, AlarmType.CHAT_ROOM_INVITATION_ACCEPTED)

        // 초대된 채팅방에 입장
        val invitedChatRoom = alarm.invitedChatRoom ?: throw ChatRoomNotFoundException()
        chatRoomCommandService.enterChatRoom(invitedChatRoom.id, memberId)
    }

    @Transactional
    fun rejectInvitation(
        chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ) {
        val alarmId = chatRoomInvitationHandlerDto.alarmId
        // 알람 타입을 거절로 변경
        alarmCommandService.changeAlarmType(alarmId, AlarmType.CHAT_ROOM_INVITATION_REJECTED)
    }
}
