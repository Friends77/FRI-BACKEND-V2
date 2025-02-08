package com.friends.category.controller

import com.friends.category.dto.CategoryListResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Category", description = "카테고리 API")
interface CategoryControllerSpec {
    @Operation(
        summary = "카테고리 리스트 조회",
        description = "카테고리 리스트를 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "카테고리 리스트 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CategoryListResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getCategoryList(): ResponseEntity<List<CategoryListResponse>>
}
