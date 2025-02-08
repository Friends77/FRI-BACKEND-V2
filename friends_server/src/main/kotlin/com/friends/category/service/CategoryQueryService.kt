package com.friends.category.service

import com.friends.category.dto.CategoryListResponse
import com.friends.category.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryQueryService(
    private val categoryRepository: CategoryRepository,
) {
    fun getCategoryList(): List<CategoryListResponse> = categoryRepository.findAll().map { CategoryListResponse(it.id, it.name, it.type, it.image) }
}
