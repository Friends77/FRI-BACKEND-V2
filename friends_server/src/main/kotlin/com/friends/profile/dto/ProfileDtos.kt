package com.friends.profile.dto

import com.friends.category.entity.Category
import com.friends.common.annotation.NullOrNotBlank
import com.friends.common.exception.ErrorCode
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.MbtiEnum
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

//작성용 dto
data class ProfileCreateDto(
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: Location? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: MutableSet<Long> = mutableSetOf(),
    var imageUrl: String? = null,
)

//수정가능한 필드 dto
data class ProfileUpdateDto(
    var nickname: String,
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: Location? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: MutableSet<Long> = mutableSetOf(),
    @Schema(description = "프로필 이미지 URL, null이면 기본 이미지로 변경.")
    @field:NullOrNotBlank(errorCode = ErrorCode.PROFILE_URL_NOT_BLANK)
    var imageUrl: String? = null,
)

//조회용 dto
data class ProfileResponseDto(
    val memberId: Long,
    val nickname: String,
    val email: String,
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: Location? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: List<Category> = listOf(),
    var imageUrl: String,
)

data class ProfileSimpleResponseDto(
    val memberId: Long,
    val nickname: String,
    val imageUrl: String,
    val selfDescription: String?,
)

data class ProfileWithDistanceQueryDto(
    val id: Long,
    val nickname: String,
    val imageUrl: String? = null,
    val distance: Double,
)

data class ProfileWithDistanceResponseDto(
    val id: Long,
    val nickname: String,
    val imageUrl: String,
    val distance: Double,
)

data class ProfileWithCategoriesResponseDto(
    val id: Long,
    val nickname: String,
    val imageUrl: String,
    val categoryIds: List<Long>,
)
