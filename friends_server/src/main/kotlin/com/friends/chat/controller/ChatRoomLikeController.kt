package com.friends.chat.controller

import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.chat.service.ChatRoomLikeCommandService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/chat/room/{chatRoomId}/like")
class ChatRoomLikeController(
    private val chatRoomLikeCommandService: ChatRoomLikeCommandService,
) : ChatRoomLikeControllerSpec {
    @PostMapping
    override fun toggleLike(
        @PathVariable("chatRoomId")
        chatRoomId: Long,
        memberId: Long,
    ): ResponseEntity<ToggleLikeResponseDto> = ResponseEntity.ok(chatRoomLikeCommandService.toggleLike(chatRoomId, memberId))
}
