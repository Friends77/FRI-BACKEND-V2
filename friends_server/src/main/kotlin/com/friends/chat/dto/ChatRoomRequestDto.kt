package com.friends.chat.dto

import com.friends.common.annotation.NullOrNotBlank
import com.friends.common.exception.ErrorCode
import io.swagger.v3.oas.annotations.media.Schema

data class ChatRoomCreateRequestDto(
    @Schema(description = "채팅방 제목")
    val title: String,
    @Schema(description = "채팅방 카테고리 ID 리스트")
    val categoryIdList: Set<Long>,
    @field:NullOrNotBlank(errorCode = ErrorCode.NOT_BLANK_CHAT_ROOM_DESCRIPTION)
    val description: String?,
)

data class ChatRoomUpdateRequestDto(
    @Schema(description = "안 바뀌면 null로 보내주세요.")
    @field:NullOrNotBlank(errorCode = ErrorCode.CHAT_ROOM_TITLE_BLANK) // null 허용, 공백은 안됨
    val title: String?,
    @Schema(description = "안 바뀌면 null로 보내주세요. 바뀌면 카테고리 ID 리스트 전체를 보내주세요.")
    val categoryIdList: Set<Long>?,
    @Schema(description = "기존 배경 이미지 삭제시 true, 변경 안 할 시 false)")
    val backgroundImageDelete: Boolean,
    @field:NullOrNotBlank(errorCode = ErrorCode.NOT_BLANK_CHAT_ROOM_DESCRIPTION)
    val description: String?,
)

data class ChatRoomInvitationRequestDto(
    @Schema(description = "초대할 채팅방의 id")
    val chatRoomId: Long,
    @Schema(description = "초대할 사용자 ID 리스트")
    val receiverIdList: Set<Long>,
)

data class ChatRoomInvitationHandlerDto(
    @Schema(defaultValue = "초대받은 알람의 id")
    val alarmId: Long,
)
