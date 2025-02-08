package com.friends.board.service

import com.friends.board.dto.BoardResponseDto
import com.friends.board.dto.CommentResponseDto
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.repository.BoardCategoryRepository
import com.friends.board.repository.BoardRepository
import com.friends.common.mapper.toCategoryInfoResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardQueryService(
    private val boardRepository: BoardRepository,
    private val boardCategoryRepository: BoardCategoryRepository,
) {
    //상세조회
    @Transactional(readOnly = true)
    fun getBoard(id: Long): BoardResponseDto {
        val board =
            boardRepository.findById(id)
                .orElseThrow { BoardNotFoundException() }
        val categories = boardCategoryRepository.findByBoardId(board.id).map { it.category }

        val boardDto =
            BoardResponseDto(
                content = board.content,
                categories = categories.map { toCategoryInfoResponse(it) },
                comments =
                    board.comments.map { comment ->
                        CommentResponseDto(
                            board = comment.board,
                            member = comment.member,
                            text = comment.text,
                        )
                    },
            )
        return boardDto
    }

    //전체조회
    fun getBoardList(pageable: Pageable): Page<BoardResponseDto> {
        val boards = boardRepository.findAll(pageable)
        return boards.map { board ->
            val categories = boardCategoryRepository.findByBoardId(board.id).map { it.category }
            BoardResponseDto(
                content = board.content,
                categories = categories.map { toCategoryInfoResponse(it) },
                comments =
                    board.comments.map { comment ->
                        CommentResponseDto(
                            board = comment.board,
                            member = comment.member,
                            text = comment.text,
                        )
                    },
            )
        }
    }
}
