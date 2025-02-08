package com.friends.board.controller

import com.friends.board.dto.CommentAddDto
import com.friends.board.dto.CommentResponseDto
import com.friends.board.dto.CommentUpdateDto
import com.friends.board.service.CommentCommandService
import com.friends.board.service.CommentQueryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    val commentCommandService: CommentCommandService,
    val commentQueryService: CommentQueryService,
) : CommentControllerSpec {
    // 댓글 작성
    @PostMapping("api/user/board/{boardId}/comments")
    override fun createComment(
        @PathVariable boardId: Long,
        @RequestBody request: CommentAddDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void> {
        commentCommandService.createComment(boardId, request, memberId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    // 댓글 삭제
    @DeleteMapping("api/user/board/{boardId}/comments/{commentId}")
    override fun deleteComment(
        @PathVariable boardId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void> {
        commentCommandService.deleteComment(boardId, commentId, memberId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    // 댓글 수정
    @PatchMapping("api/user/board/{boardId}/comments/{commentId}")
    override fun updateComment(
        @PathVariable boardId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal memberId: Long,
        @RequestBody request: CommentUpdateDto,
    ): ResponseEntity<Void> {
        commentCommandService.updateComment(boardId, commentId, request, memberId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    // 댓글 조회
    @GetMapping("api/user/board/{boardId}/comments")
    override fun getComments(
        @PathVariable boardId: Long,
    ): List<CommentResponseDto> = commentQueryService.getCommentList(boardId)
}
