package com.friends.message.service

import com.friends.chat.ChatRoomMemberNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.dto.MessageResponseDto
import com.friends.message.dto.mapper.toMessageResponseDto
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MessageQueryService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) {
    fun getUnreadMessage(
        memberId: Long,
        chatRoomId: Long,
        lastMessageId: Long?,
        size: Int,
    ): SliceBaseResponse<MessageResponseDto> {
        val member = memberRepository.findById(memberId).orElse(null) ?: throw MemberNotFoundException()
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: throw ChatRoomNotFoundException()
        // 채팅방에 속한 멤버인지 확인하는 유효성 검사도 같이 수행합니다.
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: throw ChatRoomMemberNotFoundException()

        val messages = messageRepository.findUnreadMessagesForMemberBeforeId(chatRoomMember, lastMessageId, size).map { toMessageResponseDto(it) }
        return toSliceBaseResponse(messages)
    }

    fun getPreviousMessages(
        chatRoomId: Long,
        memberId: Long,
        messageId: Long?,
        size: Int,
    ): SliceBaseResponse<MessageResponseDto> {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: throw ChatRoomNotFoundException()
        val member = memberRepository.findById(memberId).orElse(null) ?: throw MemberNotFoundException()

        // 채팅방에 속한 멤버인지 확인합니다.
        if (!chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, member)) {
            throw ChatRoomMemberNotFoundException()
        }

        /**
         * 이전 메세지를 들을 가져와 역순으로 반환합니다.
         * 이전 메세지 조회 sql 특성상 hasNext 가 동작하기 위해서는 id 를 내림차순으로 정렬해야합니다.
         * 하지만 클라이언트에서는 message id 를 오름차순으로 정렬하여 보여주기 때문에 역순으로 반환합니다.
         */
        val messages = messageRepository.findMessagesBeforeIdInChatRoom(chatRoom, messageId, size).map { toMessageResponseDto(it) }
        return toSliceBaseResponse(messages)
    }
}
