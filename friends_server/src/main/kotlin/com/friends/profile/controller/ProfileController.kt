package com.friends.profile.controller

import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.service.ProfileCommandService
import com.friends.profile.service.ProfileQueryService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileCommandService: ProfileCommandService,
    private val profileQueryService: ProfileQueryService,
) : ProfileControllerSpec {
    //내 프로필 조회
    @GetMapping("api/user/profile")
    override fun getMyProfile(
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<ProfileResponseDto> {
        val profile = profileQueryService.getProfile(memberId)
        return ResponseEntity.ok(profile)
    }

    //다른 사람 프로필 조회
    @GetMapping("api/global/profile/{memberId}")
    override fun getOtherProfile(
        @PathVariable memberId: Long,
    ): ResponseEntity<ProfileResponseDto> {
        val profile = profileQueryService.getProfile(memberId)
        return ResponseEntity.ok(profile)
    }

    //프로필 작성(초기화면) -> 회원가입 시 초기 프로필 작성이 같이 이뤄짐으로 주석 처리

//    @PostMapping(
//        value = ["api/user/profile"],
//        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
//    )
//    override fun createProfile(
//        @AuthenticationPrincipal memberId: Long,
//        @RequestPart("profileData") @Valid profileCreateDto: ProfileCreateDto,
//        @RequestPart("profileImage") profileImage: MultipartFile?,
//    ): ResponseEntity<Void> {
//        profileCommandService.createProfile(memberId, profileCreateDto, profileImage)
//        return ResponseEntity.noContent().build()
//    }

    //프로필 수정
    @PutMapping("api/user/profile")
    override fun updateProfile(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody
        @Valid
        profileUpdateDto: ProfileUpdateDto,
    ): ResponseEntity<Void> {
        profileCommandService.updateProfile(memberId, profileUpdateDto)
        return ResponseEntity.noContent().build()
    }
}
