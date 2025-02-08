package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomInvitationHandlerDto
import com.friends.chat.dto.ChatRoomInvitationRequestDto
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Chat Invitation")
interface ChatRoomInvitationControllerSpec {
    @Operation(
        description = "채팅방 초대 요청 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 초대 요청 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_A_MEMBER_OF_CHAT_ROOM,

        ],
    )
    fun requestInvitation(
        memberId: Long,
        chatRoomInvitationRequestDto: ChatRoomInvitationRequestDto,
    ): ResponseEntity<String>

    @Operation(
        description = "채팅방 초대 수락 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 초대 수락 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.ALARM_NOT_FOUND,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.MESSAGE_NOT_FOUND,
        ],
    )
    fun acceptInvitation(
        memberId: Long,
        chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ): ResponseEntity<String>

    @Operation(
        description = "채팅방 초대 거절 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 초대 거절 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.ALARM_NOT_FOUND,
        ],
    )
    fun rejectInvitation(chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto): ResponseEntity<String>
}
