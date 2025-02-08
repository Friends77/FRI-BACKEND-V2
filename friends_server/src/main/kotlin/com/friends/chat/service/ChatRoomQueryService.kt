package com.friends.chat.service

import com.friends.chat.ChatRoomMemberNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.ChatRoomMemberInfoResponseDto
import com.friends.chat.dto.mapper.ChatRoomResponseMapper
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.friendship.entity.FriendshipRequestStatusEnums
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.repository.MessageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomQueryService(
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomLikeRepository: ChatRoomLikeRepository,
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendShipRepository,
    @Value("\${image.chat-room-base-url}")
    private val chatRoomBaseImageUrl: String,
    @Value("\${image.profile-base-url}")
    private val profileBaseImageUrl: String,
    private val chatRoomResponseMapper: ChatRoomResponseMapper,
) {
    @Transactional
    fun getChatRooms(
        memberId: Long,
        nickname: String?,
    ): List<ChatRoomInfoResponseDto> {
        memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        val friends: List<Member>? =
            if (nickname != null) {
                friendshipRepository.findFriendshipByMemberIdAndNickname(memberId, nickname).also { if (it.isEmpty()) return emptyList() }
            } else {
                null
            }
        val chatRoomInfoResponse =
            chatRoomMemberRepository
                .findAllByMemberAndFriends(memberId, friends)
                .map {
                    val lastMessage = messageRepository.findRecentMessageInChatRoom(it.chatRoom)
                    chatRoomResponseMapper.toChatRoomInfoResponse(
                        it,
                        chatRoomMemberRepository.countByChatRoom(it.chatRoom),
                        chatRoomMemberRepository.findRepresentativeProfileByChatRoomId(it.chatRoom.id).map { member -> member.profile?.imageUrl ?: profileBaseImageUrl },
                        messageRepository.countUnreadMessages(it).let { count -> if (count > 999) 999 else count },
                        lastMessage,
                        it.chatRoom.imageUrl ?: chatRoomBaseImageUrl,
                    )
                } //해당 채팅방 멤버 수와 읽지 않은 메세지 수를 가져옴
        return chatRoomInfoResponse
    }

    @Transactional(readOnly = true)
    fun getChatRoomDetail(
        chatRoomId: Long,
        memberId: Long,
    ): ChatRoomDetailResponseDto {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        val participantCount = chatRoomMemberRepository.countByChatRoom(chatRoom)
        val isLike = chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom, memberId)
        val lastReadMessageId = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom = chatRoom, member = member)?.lastReadMessage?.id
        val lastMessage = messageRepository.findRecentMessageInChatRoomWithSystemMessage(chatRoom)
        return chatRoomResponseMapper.toChatRoomDetailResponseDto(
            chatRoom = chatRoom,
            memberCount = participantCount,
            isLike = isLike,
            imageUrl = chatRoom.imageUrl ?: chatRoomBaseImageUrl,
            lastReadMessageId = lastReadMessageId,
            lastMessageId = lastMessage?.id,
        )
    }

    @Transactional(readOnly = true)
    fun isUserInChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ): Boolean {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        return chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, member)
    }

    @Transactional(readOnly = true)
    fun getChatRoomMemberInfoList(
        chatRoomId: Long,
        memberId: Long,
    ): List<ChatRoomMemberInfoResponseDto> {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        if (!chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, member)) {
            throw ChatRoomMemberNotFoundException()
        }
        val isManager = chatRoom.manager == member
        var manager: Member? = null
        val chatRoomMemberList =
            mutableListOf(chatRoomResponseMapper.toChatRoomMemberInfoResponseDto(member, FriendshipRequestStatusEnums.UNAVAILABLE, isManager, true))
                .also {
                    if (!isManager) {
                        manager = memberRepository.findById(chatRoom.manager.id).orElseThrow { throw MemberNotFoundException() }
                        it.add(chatRoomResponseMapper.toChatRoomMemberInfoResponseDto(manager!!, memberFriendshipStatus(member, manager!!), isManager = true, isMe = false))
                    }
                }
        val members =
            chatRoomMemberRepository
                .findMemberByChatRoomAndMemberExceptManager(chatRoom, member, manager)
                .map {
                    chatRoomResponseMapper.toChatRoomMemberInfoResponseDto(
                        it,
                        friendshipRequestStatusEnums = memberFriendshipStatus(member, it),
                        isManager = false,
                        isMe = false,
                    )
                }
        chatRoomMemberList.addAll(members)
        return chatRoomMemberList
    }

    @Transactional(readOnly = true)
    fun getChatRoomMemberInfo(
        chatRoomId: Long,
        requesterId: Long,
        newMemberId: Long,
    ): ChatRoomMemberInfoResponseDto {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        val requester = memberRepository.findById(requesterId).orElseThrow { throw MemberNotFoundException() }
        val newMember = memberRepository.findById(newMemberId).orElseThrow { throw MemberNotFoundException() }
        if (!chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, newMember) || !chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, requester)) {
            throw ChatRoomMemberNotFoundException()
        }
        return chatRoomResponseMapper.toChatRoomMemberInfoResponseDto(newMember, memberFriendshipStatus(member = requester, friend = newMember), isManager = false, isMe = false)
    }

    private fun memberFriendshipStatus(
        member: Member,
        friend: Member,
    ): FriendshipRequestStatusEnums {
        val requestFriendship = friendshipRepository.findByRequesterAndReceiver(member, friend)
        if (requestFriendship != null) {
            return if (requestFriendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
                FriendshipRequestStatusEnums.REQUESTED
            } else {
                FriendshipRequestStatusEnums.UNAVAILABLE
            }
        }
        val receiveFriendship = friendshipRepository.findByRequesterAndReceiver(friend, member)
        if (receiveFriendship != null) {
            return if (receiveFriendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
                FriendshipRequestStatusEnums.RECEIVED
            } else {
                FriendshipRequestStatusEnums.UNAVAILABLE
            }
        }
        return FriendshipRequestStatusEnums.AVAILABLE
    }
}
