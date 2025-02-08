package com.friends.board.controller

import com.friends.board.dto.CommentAddDto
import com.friends.board.dto.CommentResponseDto
import com.friends.board.dto.CommentUpdateDto
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestPart

@Tag(name = "Comment")
interface CommentControllerSpec {
    @Operation(
        description = "댓글 생성 API",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "댓글 생성 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_COMMENT_ACCESS,
        ],
    )
    fun createComment(
        @PathVariable boardId: Long,
        @RequestPart request: CommentAddDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "댓글 삭제 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "댓글 삭제 완료",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_COMMENT_ACCESS,
        ],
    )
    fun deleteComment(
        @PathVariable boardId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "댓글 수정 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "댓글 수정 완료",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_COMMENT_ACCESS,
        ],
    )
    fun updateComment(
        @PathVariable boardId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal memberId: Long,
        @RequestPart request: CommentUpdateDto,
    ): ResponseEntity<Void>

    @Operation(
        description = "댓글 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "댓글 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = CommentResponseDto::class),
                    ),
                ],
            ),
        ],
    )
    fun getComments(
        @PathVariable boardId: Long,
    ): List<CommentResponseDto>
}
