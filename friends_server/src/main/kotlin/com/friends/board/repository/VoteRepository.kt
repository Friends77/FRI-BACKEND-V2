package com.friends.board.repository

import com.friends.board.entity.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, Long> {
    fun findByBoardId(boardId: Long): Vote?
}
