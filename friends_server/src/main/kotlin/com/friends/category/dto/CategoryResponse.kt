package com.friends.category.dto
import com.friends.category.entity.CategoryType
import io.swagger.v3.oas.annotations.media.Schema

data class CategoryInfoResponse(
    @Schema(description = "카테고리 ID")
    val id: Long,
    @Schema(description = "카테고리 이름")
    val name: String,
    @Schema(description = "카테고리 타입")
    val type: CategoryType,
    @Schema(description = "카테고리 아이콘")
    val image: String?,
)

data class CategoryListResponse(
    @Schema(description = "카테고리 ID", example = "2")
    val id: Long,
    @Schema(description = "카테고리 이름", example = "팬덤")
    val name: String,
    @Schema(description = "카테고리 타입", example = "SUBJECT")
    val type: CategoryType,
    @Schema(description = "카테고리 이미지", example = "🎈")
    val image: String?,
)
