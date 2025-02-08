package com.friends.image.controller

import com.friends.image.service.ImageCommandService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user/image")
class ImageController(
    private val imageCommandService: ImageCommandService,
) : ImageControllerSpec {
    @PostMapping(consumes = ["multipart/form-data"])
    override fun uploadImage(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestPart
        image: MultipartFile,
    ): ResponseEntity<String> = ResponseEntity.ok(imageCommandService.uploadImage(memberId, image))
}
