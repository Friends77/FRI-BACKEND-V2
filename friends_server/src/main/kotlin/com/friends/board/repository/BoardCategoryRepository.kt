package com.friends.board.repository

import com.friends.board.entity.BoardCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardCategoryRepository : JpaRepository<BoardCategory, Long> {
    fun findByBoardId(id: Long): List<BoardCategory>

    fun deleteByBoardId(id: Long)
}
