package com.friends.board.controller

import com.friends.board.dto.BoardRequestDto
import com.friends.board.dto.BoardResponseDto
import com.friends.board.entity.Board
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Board", description = "게시판 API")
interface BoardControllerSpec {
    @Operation(
        description = "게시판 등록",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "게시판 등록 성공",
            ),
        ],
    )
    fun createBoard(
        @RequestBody @Valid boardAddDto: BoardRequestDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "게시판 상세 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "게시판 상세 조회 성공",
            ),
        ],
    )
    fun getBoard(
        @PathVariable id: Long,
    ): ResponseEntity<BoardResponseDto>

    @Operation(
        description = "게시판 삭제",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "게시판 삭제 성공",
            ),
        ],
    )
    fun deleteBoard(
        @PathVariable id: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "게시판 수정",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "게시판 수정 성공",
            ),
        ],
    )
    fun updateBoard(
        @PathVariable id: Long,
        @RequestBody boardUpdateDto: BoardRequestDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Board>

    @Operation(
        description = "게시판 전체 조회",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "게시판 전체 조회 성공",
            ),
        ],
    )
    fun getBoards(
        @RequestParam page: Int,
        @RequestParam pageSize: Int,
    ): Page<BoardResponseDto>
}
