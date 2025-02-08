package com.friends.recommendation.controller

import com.friends.chat.dto.ChatRoomRecommendationResponseDto
import com.friends.common.dto.ListBaseResponse
import com.friends.profile.dto.ProfileSimpleResponseDto
import com.friends.profile.dto.ProfileWithCategoriesResponseDto
import com.friends.recommendation.service.GlobalRecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/global/recommendation")
class GlobalRecommendationController(
    private val globalRecommendationService: GlobalRecommendationService,
) : GlobalRecommendationControllerSpec {
    @GetMapping("/user/category")
    override fun getRecommendationByUserCategory(
        @RequestParam categoryIds: List<Long>,
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileWithCategoriesResponseDto>> {
        val result = globalRecommendationService.getRecommendationByUserCategory(categoryIds, size)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/chat/category")
    override fun getRecommendationByChatCategory(
        @RequestParam categoryIds: List<Long>,
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<ListBaseResponse<ChatRoomRecommendationResponseDto>> {
        val result = globalRecommendationService.getRecommendationByChatCategory(categoryIds, size)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/user")
    override fun getUserRecommendation(
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileSimpleResponseDto>> {
        val result = globalRecommendationService.getUserRecommendation(size)
        return ResponseEntity.ok(result)
    }
}
