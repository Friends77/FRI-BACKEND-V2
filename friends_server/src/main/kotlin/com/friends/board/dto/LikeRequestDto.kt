package com.friends.board.dto

import com.friends.board.entity.Board
import com.friends.member.entity.Member

data class LikeRequestDto(
    val member: Member,
    val board: Board,
)
