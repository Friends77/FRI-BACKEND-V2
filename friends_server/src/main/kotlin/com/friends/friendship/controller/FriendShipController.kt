package com.friends.friendship.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.friendship.dto.FriendShipReceiveDto
import com.friends.friendship.dto.FriendShipRequestDto
import com.friends.friendship.service.FriendShipCommandService
import com.friends.friendship.service.FriendShipQueryService
import com.friends.profile.dto.ProfileSimpleResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/friendship")
class FriendShipController(
    private val friendShipCommandService: FriendShipCommandService,
    private val friendShipQueryService: FriendShipQueryService,
) : FriendShipControllerSpec {
    @GetMapping
    override fun getFriendShip(
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<ListBaseResponse<ProfileSimpleResponseDto>> {
        val result = friendShipQueryService.getFriendshipList(memberId)
        return ResponseEntity.ok(ListBaseResponse(result))
    }

    @GetMapping("/chatRoom/{chatRoomId}/invite-list")
    override fun getFriendShipChatRoomInvite(
        @PathVariable chatRoomId: Long,
        @AuthenticationPrincipal memberId: Long,
        @RequestParam("nickname", required = false) nickname: String?,
    ): ResponseEntity<ListBaseResponse<ProfileSimpleResponseDto>> {
        val result = friendShipQueryService.getFriendshipListNotInChatRoomByName(memberId, chatRoomId, nickname)
        return ResponseEntity.ok(ListBaseResponse(result))
    }

    @PostMapping("/request")
    override fun requestFriend(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody friendShipRequestDto: FriendShipRequestDto,
    ): ResponseEntity<Void> {
        friendShipCommandService.requestFriendship(requesterId = memberId, receiverId = friendShipRequestDto.receiverId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/accept")
    override fun acceptFriendRequest(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody friendShipReceiveDto: FriendShipReceiveDto,
    ): ResponseEntity<Void> {
        friendShipCommandService.acceptFriendship(memberId, friendShipReceiveDto)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/reject")
    override fun rejectFriendRequest(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody friendShipReceiveDto: FriendShipReceiveDto,
    ): ResponseEntity<Void> {
        friendShipCommandService.rejectFriendship(memberId, friendShipReceiveDto)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/block")
    override fun blockFriendRequest(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody friendShipReceiveDto: FriendShipReceiveDto,
    ): ResponseEntity<Void> {
        friendShipCommandService.blockFriendship(memberId, friendShipReceiveDto)
        return ResponseEntity.noContent().build()
    }
}
