package com.friends.board.repository

import com.friends.board.entity.Board
import com.friends.board.entity.Like
import com.friends.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long> {
    fun findByMemberAndBoard(
        member: Member,
        board: Board,
    ): Like?
}
