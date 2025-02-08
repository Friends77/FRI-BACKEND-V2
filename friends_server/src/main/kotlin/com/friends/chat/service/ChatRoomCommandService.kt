package com.friends.chat.service

import com.friends.category.repository.CategoryRepository
import com.friends.chat.ChatRoomBaseImageCannotDeleteException
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.ChatRoomMustHaveCategoryException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.ChatRoomUpdateException
import com.friends.chat.NotChatRoomManagerException
import com.friends.chat.NotChatRoomMemberException
import com.friends.chat.NotForceLeaveYourselfException
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomUpdateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.repository.ChatRoomCategoryRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.annotation.DistributedLock
import com.friends.common.exception.ErrorCode
import com.friends.common.exception.ParameterValidationException
import com.friends.common.key.CHAT_ROOM_LIKE_LOCK
import com.friends.image.S3ClientService
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.message.repository.MessageRepository
import com.friends.message.service.MessageCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ChatRoomCommandService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val chatRoomCategoryRepository: ChatRoomCategoryRepository,
    private val s3ClientService: S3ClientService,
    private val messageCommandService: MessageCommandService,
    private val messageRepository: MessageRepository,
) {
    @Transactional
    fun createChatRoom(
        request: ChatRoomCreateRequestDto,
        memberId: Long,
        backgroundImage: MultipartFile?,
    ): CreateChatRoomResponseDto {
        if (request.title.isBlank()) throw ParameterValidationException(errorCode = ErrorCode.NOT_BLANK_CHAT_ROOM_DESCRIPTION)
        if (request.categoryIdList.isEmpty()) throw ParameterValidationException(errorCode = ErrorCode.CHAT_ROOM_CATEGORY_INVALID_SIZE)
        val imageUrl =
            backgroundImage?.let {
                s3ClientService.upload(it)
            }
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        val chatRoom = chatRoomRepository.save(ChatRoom.of(request.title, member, imageUrl, request.description))
        chatRoomCategoryRepository.saveAll(categoryRepository.findByIdIn(request.categoryIdList).also { if (it.isEmpty()) throw ChatRoomCategoryNotFoundException() }.map { ChatRoomCategory.of(chatRoom, it) })
        val enterMassage = messageCommandService.sendMessage(chatRoom.id, member.id, Message.enterMessage(member.nickname), MessageType.SYSTEM_MEMBER_ENTER)
        messageCommandService.setChatRoomOnline(chatRoom.id, memberId)
        chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom, member, enterMassage))
        return CreateChatRoomResponseDto(chatRoom.id)
    }

    @Transactional
    fun enterChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        if (!chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, member)) {
            val enterMessage = messageCommandService.sendMessage(chatRoom.id, member.id, Message.enterMessage(member.nickname), MessageType.SYSTEM_MEMBER_ENTER)
            messageCommandService.setChatRoomOnline(chatRoomId, memberId)
            chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom, member, enterMessage))
        }
    }

    @Transactional
    fun leaveChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ) {
        // postgreSQL에서는 격리수준 default가 read committed이므로,
        // 만일 채팅방의 최후 2인이 동시에 나갈 경우, 각 트랜잭션에선 본인이 나가더라도 1명이 남아있을 것으로 잘못 판단하고 방이 사라지지않는 문제가 발생할 수 있습니다.
        // 따라서, chatRoom에 비관적 베타락을 걸어서 한 요청을 처리하는 동안 다른 트랜잭션이 chatRoom에 접근하지 못하도록 합니다.
        val chatRoom = chatRoomRepository.findByIdWithLock(chatRoomId) ?: throw ChatRoomNotFoundException()
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: throw NotChatRoomMemberException()
        chatRoomMemberRepository.delete(chatRoomMember)
        messageCommandService.setChatRoomOffline(memberId, chatRoomId) // 채팅방에서 나가면 온라인 유저에서도 제거
        if (chatRoomMemberRepository.countByChatRoom(chatRoom) == 0) {
            deleteChatRoom(chatRoom)
        } else {
            messageCommandService.sendMessage(chatRoomId, memberId, Message.exitMessage(member.nickname), MessageType.SYSTEM_MEMBER_LEAVE) // 채팅방에 나갔다는 메세지 전송
            if (chatRoom.manager == member) {
                val newManager = chatRoomMemberRepository.findFirstByChatRoomOrderByCreatedAt(chatRoom).member
                chatRoom.changeManager(newManager)
                messageCommandService.sendMessage(chatRoomId, newManager.id, Message.changeManagerMessage(newManager.nickname), MessageType.SYSTEM_NEW_MANAGER) // 새로운 매니저에게 매니저 변경 메세지 전송
            }
        }
    }

    private fun deleteChatRoom(chatRoom: ChatRoom) {
        messageRepository.deleteByChatRoom(chatRoom)
        chatRoomCategoryRepository.deleteByChatRoom(chatRoom)
        chatRoomRepository.delete(chatRoom)
    }

    @Transactional
    @DistributedLock(lockName = CHAT_ROOM_LIKE_LOCK, identifier = "chatRoomId")
    fun updateChatRoom(
        chatRoomId: Long,
        request: ChatRoomUpdateRequestDto?,
        memberId: Long,
        backgroundImage: MultipartFile?,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        if (chatRoom.manager.id != memberId) throw NotChatRoomManagerException()
        val changeImageUpdate = updateChatRoomImageUrl(chatRoom, request, backgroundImage)
        val changeChatRoomInfo =
            if (request != null) {
                updateChatRoomInfo(chatRoom, request)
            } else {
                false
            }
        if (!changeImageUpdate && !changeChatRoomInfo) throw ChatRoomUpdateException()
    }

    @Transactional
    fun forcedToLeave(
        chatRoomId: Long,
        memberId: Long,
        forceLeaveMemberId: Long,
    ) {
        if (memberId == forceLeaveMemberId) throw NotForceLeaveYourselfException()
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        val forceLeaveMember = memberRepository.findById(forceLeaveMemberId).orElseThrow { throw MemberNotFoundException() }
        if (chatRoom.manager.id != memberId) throw NotChatRoomManagerException()
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, forceLeaveMember) ?: throw NotChatRoomMemberException()
        chatRoomMemberRepository.delete(chatRoomMember)
        messageCommandService.setChatRoomOffline(forceLeaveMemberId, chatRoomId)
        messageCommandService.sendMessage(chatRoomId, memberId, Message.forceExitMessage(forceLeaveMember.nickname), MessageType.SYSTEM_MEMBER_LEAVE)
    }

    private fun updateChatRoomImageUrl(
        chatRoom: ChatRoom,
        request: ChatRoomUpdateRequestDto?,
        backgroundImage: MultipartFile?,
    ): Boolean {
        if (backgroundImage != null) {
            if (chatRoom.imageUrl != null) {
                s3ClientService.deleteS3Object(chatRoom.imageUrl!!)
            }
            chatRoom.imageUrl = s3ClientService.upload(backgroundImage)
            return true
        } else {
            if (request != null && request.backgroundImageDelete) {
                if (chatRoom.imageUrl == null) {
                    throw ChatRoomBaseImageCannotDeleteException()
                }
                s3ClientService.deleteS3Object(chatRoom.imageUrl!!)
                chatRoom.imageUrl = null
                return true
            }
        }
        return false
    }

    private fun updateChatRoomInfo(
        chatRoom: ChatRoom,
        request: ChatRoomUpdateRequestDto,
    ): Boolean {
        var changeChatRoomInfo = false
        if (request.title != null && chatRoom.title != request.title) {
            chatRoom.title = request.title
            changeChatRoomInfo = true
        }
        if (request.description != null && chatRoom.description != request.description) {
            chatRoom.description = request.description
            changeChatRoomInfo = true
        }
        if (request.categoryIdList != null) {
            val categoryList = categoryRepository.findByIdIn(request.categoryIdList).also { if (it.isEmpty()) throw ChatRoomMustHaveCategoryException() }
            // 채팅방의 카테고리 중 없는 카테고리 ID 리스트에 포함되지 않은 ID를 필터링해서 가져오기
            val addCategoryList = categoryList.filter { it !in chatRoom.categories.map { chatRoomCategory -> chatRoomCategory.category } }
            if (addCategoryList.isNotEmpty()) {
                chatRoomCategoryRepository.saveAll(
                    addCategoryList
                        .map { ChatRoomCategory.of(chatRoom, it) },
                )
                changeChatRoomInfo = true
            }
            val categoriesToRemove = chatRoom.categories.filter { it.category !in categoryList }
            if (categoriesToRemove.isNotEmpty()) {
                chatRoomCategoryRepository.deleteAllInBatch(categoriesToRemove)
                changeChatRoomInfo = true
            }
        }

        return changeChatRoomInfo
    }
}
