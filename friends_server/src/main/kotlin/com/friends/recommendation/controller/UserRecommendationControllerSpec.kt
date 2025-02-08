package com.friends.recommendation.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.profile.dto.ProfileWithDistanceResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Recommendation - user")
interface UserRecommendationControllerSpec {
    @Operation(
        description = "거리 기반 추천 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "거리 기반 추천 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.PROFILE_NOT_FOUND,
            ErrorCode.PROFILE_LOCATION_NULL,
        ],
    )
    fun getDistanceRecommendation(
        memberId: Long,
        distance: Double,
        size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileWithDistanceResponseDto>>
}
