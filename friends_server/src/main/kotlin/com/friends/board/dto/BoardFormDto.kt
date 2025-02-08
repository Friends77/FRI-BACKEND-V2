package com.friends.board.dto

import com.friends.category.dto.CategoryInfoResponse

data class BoardRequestDto(
    var content: String,
    var categoryIds: Set<Long>,
)

data class BoardResponseDto(
    var content: String,
    var categories: List<CategoryInfoResponse>?,
    var comments: List<CommentResponseDto>?,
)
