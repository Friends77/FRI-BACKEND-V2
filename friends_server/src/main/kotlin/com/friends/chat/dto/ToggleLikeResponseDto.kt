package com.friends.chat.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ToggleLikeResponseDto(
    @Schema(description = "채팅방 ID", example = "1")
    val chatRoomId: Long,
    @Schema(description = "리뷰 좋아요 수", example = "1")
    val likeCount: Int,
    @Schema(description = "좋아요 여부(false는 좋아요 취소)", example = "true")
    val liked: Boolean,
)
