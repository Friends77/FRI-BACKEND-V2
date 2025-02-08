package com.friends.common.dto

class SliceBaseResponse<T>(
    val content: List<T>,
    val hasNext: Boolean,
)

class ListBaseResponse<T>(
    val content: List<T>,
)
