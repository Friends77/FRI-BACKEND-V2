package com.friends.category.controller

import com.friends.category.dto.CategoryListResponse
import com.friends.category.service.CategoryQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/global/category")
class CategoryController(
    private val categoryQueryService: CategoryQueryService,
) : CategoryControllerSpec {
    @GetMapping
    override fun getCategoryList(): ResponseEntity<List<CategoryListResponse>> = ResponseEntity.ok(categoryQueryService.getCategoryList())
}
