package com.friends.profile.controller

import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Profile", description = "프로필 API")
interface ProfileControllerSpec {
    @Operation(
        description = "내 프로필 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "내 프로필 조회 성공",
            ),
        ],
    )
    fun getMyProfile(
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<ProfileResponseDto>

    @Operation(
        description = "다른 사람 프로필 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "다른 사람 프로필 조회 성공",
            ),
        ],
    )
    fun getOtherProfile(
        @PathVariable memberId: Long,
    ): ResponseEntity<ProfileResponseDto>

//    @Operation(
//        description = "회원가입 후 프로필 작성",
//        responses = [
//            ApiResponse(
//                responseCode = "204",
//                description = "프로필 작성 성공",
//            ),
//        ],
//    )
//    fun createProfile(
//        @AuthenticationPrincipal memberId: Long,
//        @RequestPart @Valid profileCreateDto: ProfileCreateDto,
//        @RequestPart profileImage: MultipartFile?,
//    ): ResponseEntity<Void>

    @Operation(
        description = "프로필 수정",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "프로필 수정 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.PROFILE_URL_NOT_BLANK,
            ErrorCode.INVALID_IMAGE_URL,
        ],
    )
    fun updateProfile(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody
        @Valid
        profileUpdateDto: ProfileUpdateDto,
    ): ResponseEntity<Void>
}
