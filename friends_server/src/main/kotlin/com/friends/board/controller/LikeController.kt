package com.friends.board.controller

import com.friends.board.dto.LikeRequestDto
import com.friends.board.service.LikeCommandService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LikeController(
    val likeCommandService: LikeCommandService,
) : LikeControllerSpec {
    //좋아요 추가
    @PostMapping("api/user/board/like")
    override fun createLike(
        likeRequestDto: LikeRequestDto,
    ): ResponseEntity<Void> {
        likeCommandService.createLike(likeRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    //좋아요 삭제
    @DeleteMapping("api/user/board/like")
    override fun deleteLike(
        likeRequestDto: LikeRequestDto,
    ): ResponseEntity<Void> {
        likeCommandService.deleteLike(likeRequestDto)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
