package com.friends.common.mapper

import com.friends.category.dto.CategoryInfoResponse
import com.friends.category.entity.Category
import com.friends.common.dto.SliceBaseResponse
import org.springframework.data.domain.Slice

fun <T> toSliceBaseResponse(slice: Slice<T>): SliceBaseResponse<T> = SliceBaseResponse(content = slice.content, hasNext = slice.hasNext())

fun toCategoryInfoResponse(category: Category) = CategoryInfoResponse(category.id, category.name, category.type, category.image)
