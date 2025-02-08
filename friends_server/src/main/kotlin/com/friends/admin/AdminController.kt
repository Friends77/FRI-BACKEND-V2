package com.friends.admin

import com.friends.chat.service.ChatRoomRecommendationCriteriaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService,
    private val chatRoomRecommendationCriteriaService: ChatRoomRecommendationCriteriaService,
) : AdminControllerSpec {
    // 관리자용 이미지 업로드
    @PostMapping("/image", consumes = ["multipart/form-data"])
    override fun uploadImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("type") type: BaseImageType,
    ): ResponseEntity<String> {
        val url = adminService.uploadImage(file, type)
        return ResponseEntity.ok(url)
    }

    @DeleteMapping("/image")
    override fun deleteImage(
        @RequestParam("url") url: String,
    ): ResponseEntity<Void> {
        adminService.deleteImage(url)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/chat-room/recommendation-criteria")
    override fun aggregateChatRoomRecommendationCriteria(): ResponseEntity<Void> {
        chatRoomRecommendationCriteriaService.extractMemberMostGender()
        chatRoomRecommendationCriteriaService.extractMemberMostAgeRange()
        return ResponseEntity.noContent().build()
    }
}
