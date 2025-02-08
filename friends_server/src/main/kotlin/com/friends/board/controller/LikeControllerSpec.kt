package com.friends.board.controller

import com.friends.board.dto.LikeRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Like")
interface LikeControllerSpec {
    @Operation(
        description = "좋아요 추가 API",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "좋아요 추가 성공",
            ),
        ],
    )
    fun createLike(
        @RequestBody @Valid likeRequestDto: LikeRequestDto,
    ): ResponseEntity<Void>

    @Operation(
        description = "좋아요 취소 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "좋아요 취소 성공",
            ),
        ],
    )
    fun deleteLike(
        @RequestBody @Valid likeRequestDto: LikeRequestDto,
    ): ResponseEntity<Void>
}
