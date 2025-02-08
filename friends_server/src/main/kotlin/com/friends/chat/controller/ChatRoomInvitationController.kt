package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomInvitationHandlerDto
import com.friends.chat.dto.ChatRoomInvitationRequestDto
import com.friends.chat.service.ChatRoomInvitationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/chat/invitation")
class ChatRoomInvitationController(
    private val chatRoomInvitationService: ChatRoomInvitationService,
) : ChatRoomInvitationControllerSpec {
    @PostMapping("/request")
    override fun requestInvitation(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody chatRoomInvitationRequestDto: ChatRoomInvitationRequestDto,
    ): ResponseEntity<String> {
        chatRoomInvitationService.requestInvitation(memberId, chatRoomInvitationRequestDto)
        return ResponseEntity.ok("초대 요청이 완료되었습니다.")
    }

    @PostMapping("/accept")
    override fun acceptInvitation(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ): ResponseEntity<String> {
        chatRoomInvitationService.acceptInvitation(memberId, chatRoomInvitationHandlerDto)
        return ResponseEntity.ok("초대 수락이 완료되었습니다.")
    }

    @PostMapping("/reject")
    override fun rejectInvitation(
        @RequestBody chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ): ResponseEntity<String> {
        chatRoomInvitationService.rejectInvitation(chatRoomInvitationHandlerDto)
        return ResponseEntity.ok("초대 거절이 완료되었습니다.")
    }
}
