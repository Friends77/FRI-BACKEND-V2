package com.friends

import com.friends.category.entity.Category
import com.friends.category.entity.CategoryType

const val TEST_SIZE = 10
const val TEST_CATEGORY_NAME = "일상"
const val TEST_CATEGORY_ID = 1L

fun createTestCategory(
    id: Long = TEST_CATEGORY_ID,
    name: String = TEST_CATEGORY_NAME,
    type: CategoryType = CategoryType.SUBJECT,
) = Category(id, name, type)
