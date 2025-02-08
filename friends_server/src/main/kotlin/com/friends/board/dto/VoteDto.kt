package com.friends.board.dto

data class VoteResponseDto(
    val voteId: Long,
    val boardId: Long,
    val options: List<VoteOptionResponseDto>,
)

data class VoteOptionResponseDto(
    val optionId: Long,
    val content: String,
    val voteCount: Int,
)
