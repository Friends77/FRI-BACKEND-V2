package com.friends.message.controller

import com.friends.common.dto.SliceBaseResponse
import com.friends.message.dto.MessageResponseDto
import com.friends.message.service.MessageCommandService
import com.friends.message.service.MessageQueryService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/message")
class MessageController(
    private val messageQueryService: MessageQueryService,
    private val messageCommandService: MessageCommandService,
) : MessageControllerSpec {
    @GetMapping("/unread/{chatRoomId}")
    override fun getUnreadMessages(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("lastMessageId", required = false) lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>> {
        val result = messageQueryService.getUnreadMessage(memberId, chatRoomId, lastMessageId, size)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/previous/{chatRoomId}")
    override fun getPreviousMessage(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("lastMessageId", required = false) lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>> {
        val result = messageQueryService.getPreviousMessages(chatRoomId, memberId, lastMessageId, size)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{messageId}")
    override fun deleteMessage(
        @AuthenticationPrincipal
        memberId: Long,
        @PathVariable("messageId")
        messageId: Long,
    ): ResponseEntity<Unit> {
        messageCommandService.deleteMessage(memberId, messageId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/disconnect/{chatRoomId}")
    override fun disconnectChatRoom(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<Unit> {
        messageCommandService.disconnectChatRoom(chatRoomId, memberId)
        return ResponseEntity.noContent().build()
    }
}
