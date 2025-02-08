package com.friends.message.controller

import com.friends.common.dto.SliceBaseResponse
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.message.dto.MessageResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Message")
interface MessageControllerSpec {
    @Operation(
        description =
            "읽지 않은 메세지 조회 API <br>" +
                "로그인 된 사용자만 사용 가능합니다",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "읽지 않은 메세지 조회 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND,
        ],
    )
    fun getUnreadMessages(
        memberId: Long,
        chatRoomId: Long,
        size: Int,
        lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>>

    @Operation(
        description =
            "이전 메세지 조회 API <br>" +
                "로그인 된 사용자만 사용 가능합니다",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "이전 메세지 조회 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND,
        ],
    )
    fun getPreviousMessage(
        memberId: Long,
        chatRoomId: Long,
        size: Int,
        lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>>

    @Operation(
        description =
            "메세지 삭제 API <br>" +
                "IMAGE 타입이 삭제되는 경우도 TEXT 타입과 같이 해당 message의 type은 DELETE_MESSAGE 바뀌며, 내용도 삭제된 메세지입니다.라고 바뀝니다. <br> 메세지 삭제시, 웹소켓으로 \n {\n" +
                "    \"messageId\": 574,\n" +
                "    \"chatRoomId\": 1,\n" +
                "    \"senderId\": 4,\n" +
                "    \"content\": \"삭제된 메세지입니다.\",\n" +
                "    \"createdAt\": 1736842876516,\n" +
                "    \"type\": \"DELETE_MESSAGE\"\n" +
                "} 형태의 메세지를 전송합니다. <br> DB 상에서 내용이 바뀌지만 이미 화면상에 랜더링된 데이터는 변경되지 않으니, 클라이언트는 해당 메세지를 화면상에서 삭제되었다고 처리해야 합니다.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "메세지 삭제 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.MESSAGE_NOT_FOUND,
            ErrorCode.NOT_MESSAGE_SENDER,
        ],
    )
    fun deleteMessage(
        memberId: Long,
        messageId: Long,
    ): ResponseEntity<Unit>

    @Operation(
        description =
            "채팅방 종료 API <br>" +
                "채팅방을 종료하거나, 다른 채팅방으로 이동할 경우 사용합니다 <br>" +
                "이 API 가 보내진 시점 이후로 채팅방에 전송된 메세지는 읽지 않은 메세지가 됩니다",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "채팅방 종료하기 성공",
            ),
        ],
    )
    fun disconnectChatRoom(
        memberId: Long,
        chatRoomId: Long,
    ): ResponseEntity<Unit>
}
