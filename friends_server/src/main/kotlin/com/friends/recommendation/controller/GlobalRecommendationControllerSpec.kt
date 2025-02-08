package com.friends.recommendation.controller

import com.friends.chat.dto.ChatRoomRecommendationResponseDto
import com.friends.common.dto.ListBaseResponse
import com.friends.profile.dto.ProfileSimpleResponseDto
import com.friends.profile.dto.ProfileWithCategoriesResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Recommendation - global")
interface GlobalRecommendationControllerSpec {
    @Operation(
        description = "카테고리 기반 추천 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "카테고리 기반 추천 성공",
            ),
        ],
    )
    fun getRecommendationByUserCategory(
        categoryIds: List<Long>,
        size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileWithCategoriesResponseDto>>

    @Operation(
        description = "카테고리 기반 추천 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "카테고리 기반 추천 성공",
            ),
        ],
    )
    fun getRecommendationByChatCategory(
        categoryIds: List<Long>,
        size: Int,
    ): ResponseEntity<ListBaseResponse<ChatRoomRecommendationResponseDto>>

    @Operation(
        description = "유저 추천 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "유저 추천 성공",
            ),
        ],
    )
    fun getUserRecommendation(
        size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileSimpleResponseDto>>
}
