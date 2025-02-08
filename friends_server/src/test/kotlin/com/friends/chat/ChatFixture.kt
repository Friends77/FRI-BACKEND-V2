package com.friends.chat

import com.friends.TEST_CATEGORY_ID
import com.friends.TEST_SIZE
import com.friends.category.entity.Category
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.ChatRoomUpdateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.chat.dto.mapper.ChatRoomResponseMapper
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.entity.ChatRoomLike
import com.friends.chat.entity.ChatRoomMember
import com.friends.createTestCategory
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.message.createMockTestMessage
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import com.friends.profile.TEST_PROFILE_IMAGE_URL

const val TEST_CHAT_ROOM_ID = 1L
const val CHAT_ROOM_TITLE = "테스트 채팅방"
const val CREATE_CHAT_ROOM_REQUEST = "chatRoomCreateRequestDto"
const val CHAT_ROOM_BASE_IMAGE_URL = "chatRoomBaseImageUrl"
private val mapper = ChatRoomResponseMapper(CHAT_ROOM_BASE_IMAGE_URL)

fun createTestChatRoom(
    title: String = CHAT_ROOM_TITLE,
    manager: Member = createTestMember(),
    imageUrl: String? = null,
    likeCount: Int = 0,
    categories: List<ChatRoomCategory> = emptyList(),
    description: String? = null,
) = ChatRoom(id = 0L, title = title, manager = manager, imageUrl = imageUrl, likeCount = likeCount, categories = categories, description = description)

fun createTestChatRoomCreateRequestDto(
    title: String = CHAT_ROOM_TITLE,
    categories: Set<Long> = setOf(TEST_CATEGORY_ID),
    description: String? = null,
) = ChatRoomCreateRequestDto(title, categories, description)

fun createTestChatRoomMember(
    chatRoom: ChatRoom = createTestChatRoom(),
    member: Member = createTestMember(),
    lastReadMessage: Message = createTestMessage(),
) = ChatRoomMember.of(
    chatRoom,
    member,
    lastReadMessage,
)

fun createTestChatRoomList(
    chatRoomMember: ChatRoomMember = ChatRoomMember.of(createTestChatRoom(), createTestMember(), createMockTestMessage()),
) = listOf(chatRoomMember)

fun createTestChatRoomInfoResponseDto(
    chatRoomMember: ChatRoomMember = createTestChatRoomMember(),
    memberCount: Int = TEST_SIZE,
    representativeProfile: List<String> = listOf(TEST_PROFILE_IMAGE_URL),
    unreadMessageCount: Int = 0,
    lastReadMessage: Message = createMockTestMessage(),
    imageUrl: String = CHAT_ROOM_BASE_IMAGE_URL,
): ChatRoomInfoResponseDto = mapper.toChatRoomInfoResponse(chatRoomMember, memberCount, representativeProfile, unreadMessageCount, lastReadMessage, imageUrl)

fun createTestChatRoomMemberList(
    chatRoomList: List<ChatRoomMember> = createTestChatRoomList(),
) = chatRoomList.map { createTestChatRoomInfoResponseDto(it) }

fun createTestToggleLikeResponseDto(
    chatRoomId: Long = TEST_CHAT_ROOM_ID,
    like: Boolean = true,
    likeCount: Int = TEST_SIZE,
) = ToggleLikeResponseDto(chatRoomId, likeCount, like)

fun createTestChatRoomLike(
    chatRoom: ChatRoom = createTestChatRoom(),
    member: Member = createTestMember(),
) = ChatRoomLike.of(chatRoom, member)

fun createTestChatRoomDetailResponseDto(
    chatRoom: ChatRoom = createTestChatRoom(),
    memberCount: Int = TEST_SIZE,
    like: Boolean = false,
    imageUrl: String = CHAT_ROOM_BASE_IMAGE_URL,
) = mapper.toChatRoomDetailResponseDto(chatRoom, memberCount, like, imageUrl, null, null)

fun createTestCreateChatRoomResponseDto(
    chatRoomId: Long = TEST_CHAT_ROOM_ID,
) = CreateChatRoomResponseDto(chatRoomId)

fun createTestChatRoomUpdateRequestDto(
    title: String? = null,
    categoryIds: Set<Long> = emptySet(),
    backgroundImageDelete: Boolean = false,
) = ChatRoomUpdateRequestDto(title, categoryIds, backgroundImageDelete, null)

fun createTestChatRoomCategory(
    chatRoom: ChatRoom = createTestChatRoom(),
    category: Category = createTestCategory(),
) = ChatRoomCategory(0L, chatRoom, category)
