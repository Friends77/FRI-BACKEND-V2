package com.friends.board.dto

import com.friends.board.entity.Board
import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member

data class CommentAddDto(
    var text: String,
    var member: Member,
    var board: Board,
) : BaseModifiableEntity()

data class CommentUpdateDto(
    var text: String,
) : BaseModifiableEntity()

data class CommentResponseDto(
    var board: Board,
    var member: Member,
    var text: String,
)
