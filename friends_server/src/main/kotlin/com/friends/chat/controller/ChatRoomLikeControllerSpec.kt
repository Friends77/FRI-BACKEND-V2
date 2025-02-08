package com.friends.chat.controller

import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.common.annotation.CustomPositive
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal

interface ChatRoomLikeControllerSpec {
    @Operation(
        summary = "채팅방 좋아요 토글",
        description = "채팅방 좋아요 토글 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 좋아요 토글 성공",
                content = [
                    Content(
                        schema =
                            Schema(
                                implementation = ToggleLikeResponseDto::class,
                            ),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.CHAT_ROOM_POSITIVE_LIKE_COUNT,
        ],
    )
    fun toggleLike(
        @CustomPositive(message = "채팅방 ID는 양수여야 합니다.")
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<ToggleLikeResponseDto>
}
