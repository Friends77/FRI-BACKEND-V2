package com.friends.board.service

import com.friends.board.dto.CommentResponseDto
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CommentQueryService(
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository,
) {
    //댓글조회
    @Transactional(readOnly = true)
    fun getCommentList(
        boardId: Long,
        lastCreatedAt: LocalDateTime? = null,
    ): List<CommentResponseDto> {
        if (!boardRepository.existsById(boardId)) {
            throw BoardNotFoundException()
        }
        return if (lastCreatedAt == null) {
            commentRepository.findTop20ByBoardIdOrderByCreatedAtAsc(boardId)
        } else {
            commentRepository.findTop20ByBoardIdAndCreatedAtAfterOrderByIdAsc(
                boardId,
                lastCreatedAt,
            )
        }
    }
}
