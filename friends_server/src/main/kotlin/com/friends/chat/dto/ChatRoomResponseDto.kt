package com.friends.chat.dto

import com.friends.category.dto.CategoryInfoResponse
import com.friends.friendship.entity.FriendshipRequestStatusEnums
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class ChatRoomInfoResponseDto(
    @Schema(description = "참여하는 채팅방 연관 ID")
    val chatRoomMemberId: Long,
    @Schema(description = "채팅방 ID")
    val id: Long,
    @Schema(description = "채팅방 제목")
    val title: String,
    @Schema(description = "채팅방 이미지 URL")
    val imageUrl: String,
    @Schema(description = "채팅방 카테고리 리스트")
    val categoryIdList: List<CategoryInfoResponse>,
    @Schema(description = "채팅방 전체 참여자 수")
    val participantCount: Int,
    @Schema(description = "채팅방 참여자 프로필 리스트(최대 4명)")
    val participantProfileList: List<String>,
    @Schema(description = "채팅방 마지막 메세지 시간")
    val lastMessageTime: LocalDateTime?,
    @Schema(description = "채팅방 마지막 메세지")
    val lastMessage: String?,
    @Schema(description = "안 읽은 메세지 수")
    val unreadMessageCount: Int,
)

data class ChatRoomDetailResponseDto(
    @Schema(description = "채팅방 ID")
    val id: Long,
    @Schema(description = "채팅방 제목")
    val title: String,
    @Schema(description = "채팅방 이미지 URL")
    val imageUrl: String,
    @Schema(description = "채팅방 카테고리 리스트")
    val categoryIdList: List<CategoryInfoResponse>,
    @Schema(description = "채팅방 참여자 수")
    val participantCount: Int,
    @Schema(description = "채팅방 좋아요 수")
    val likeCount: Int,
    @Schema(description = "해당 채팅방 좋아요 여부(좋아요 눌렀을 시, true)")
    val isLike: Boolean,
    @Schema(description = "유저가 마지막으로 읽은 메세지의 id")
    val lastReadMessageId: Long?,
    @Schema(description = "채팅방의 마지막 메세지의 id")
    val lastMessageId: Long?,
)

data class ChatRoomRecommendationResponseDto(
    @Schema(description = "채팅방 ID")
    val id: Long,
    @Schema(description = "채팅방 제목")
    val title: String,
    @Schema(description = "채팅방 이미지 URL")
    val imageUrl: String,
    @Schema(description = "채팅방 카테고리 리스트")
    val categoryIdList: List<CategoryInfoResponse>,
    @Schema(description = "채팅방 참여자 수")
    val participantCount: Int,
    @Schema(description = "채팅방 참여자 프로필 리스트(최대 4명)")
    val participantProfileList: List<String>,
    @Schema(description = "채팅방 설명")
    val description: String?,
)

data class CreateChatRoomResponseDto(
    @Schema(description = "채팅방 ID")
    val chatRoomId: Long,
)

data class ChatRoomMemberInfoResponseDto(
    @Schema(description = "채팅방 참여자 ID")
    val id: Long,
    @Schema(description = "채팅방 참여자 닉네임")
    val nickname: String,
    @Schema(description = "채팅방 참여자 프로필 이미지 URL")
    val profileImageUrl: String,
    @Schema(description = "친구 신청 상태")
    val friendshipStatusEnums: FriendshipRequestStatusEnums,
    @Schema(description = "채팅방 방장 여부")
    val isManager: Boolean,
    @Schema(description = "본인 여부")
    val isMe: Boolean,
)
