package com.friends.category.repository

import com.friends.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?

    fun findByIdIn(ids: Set<Long>): List<Category>
}
