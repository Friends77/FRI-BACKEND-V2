package com.friends.friendship.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.friendship.dto.FriendShipReceiveDto
import com.friends.friendship.dto.FriendShipRequestDto
import com.friends.profile.dto.ProfileSimpleResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "friendship", description = "친구 API")
interface FriendShipControllerSpec {
    @Operation(
        description = "친구 목록 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "친구 목록 조회 성공",
            ),
        ],
    )
    fun getFriendShip(
        memberId: Long,
    ): ResponseEntity<ListBaseResponse<ProfileSimpleResponseDto>>

    @Operation(
        description = "친구 요청 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "친구 요청 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.FRIENDSHIP_ALREADY_EXIST,
            ErrorCode.FRIENDSHIP_BLOCKED,
        ],
    )
    fun requestFriend(
        memberId: Long,
        friendShipRequestDto: FriendShipRequestDto,
    ): ResponseEntity<Void>

    @Operation(
        description = "친구 요청 수락 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "친구 요청 수락 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.FRIENDSHIP_NOT_FOUND,
            ErrorCode.FRIENDSHIP_NOT_WAITING,
        ],
    )
    fun acceptFriendRequest(
        memberId: Long,
        friendShipReceiveDto: FriendShipReceiveDto,
    ): ResponseEntity<Void>

    @Operation(
        description = "친구 요청 거절 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "친구 요청 거절 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.FRIENDSHIP_NOT_FOUND,
            ErrorCode.FRIENDSHIP_NOT_WAITING,
        ],
    )
    fun rejectFriendRequest(
        memberId: Long,
        friendShipReceiveDto: FriendShipReceiveDto,
    ): ResponseEntity<Void>

    @Operation(
        description = "친구 차단 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "친구 차단 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.FRIENDSHIP_NOT_FOUND,
            ErrorCode.FRIENDSHIP_NOT_WAITING,
        ],
    )
    fun blockFriendRequest(
        memberId: Long,
        friendShipReceiveDto: FriendShipReceiveDto,
    ): ResponseEntity<Void>

    @Operation(
        description = "채팅방에서 친구 초대 목록 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방에서 친구 초대 목록 조회 성공",
            ),
        ],
    )
    fun getFriendShipChatRoomInvite(
        chatRoomId: Long,
        memberId: Long,
        nickname: String?,
    ): ResponseEntity<ListBaseResponse<ProfileSimpleResponseDto>>
}
