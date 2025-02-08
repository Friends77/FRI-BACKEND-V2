package com.friends.board.service

import com.friends.board.dto.BoardRequestDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardCategory
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.exception.InvalidBoardAccessException
import com.friends.board.exception.NotFoundBoardCategoryException
import com.friends.board.repository.BoardCategoryRepository
import com.friends.board.repository.BoardRepository
import com.friends.category.entity.Category
import com.friends.category.repository.CategoryRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BoardCommandService(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val boardCategoryRepository: BoardCategoryRepository,
) {
    fun createBoard(
        boardAddDto: BoardRequestDto,
        requestMemberId: Long,
    ): Board {
        val member =
            memberRepository
                .findById(requestMemberId)
                .orElseThrow {
                    MemberNotFoundException()
                }

        val board =
            Board(
                member = member,
                content = boardAddDto.content,
            )

        boardRepository.save(board)

        val categories =
            categoryRepository.findByIdIn(boardAddDto.categoryIds).also {
                if (it.isEmpty()) {
                    throw NotFoundBoardCategoryException()
                }
            }
        val boardCategories =
            categories.map { category ->
                BoardCategory(
                    board = board,
                    category = Category(category.id, category.name, category.type),
                )
            }
        boardCategoryRepository.saveAll(boardCategories)
        return board
    }

    //게시글 삭제
    fun deleteBoard(
        id: Long,
        requestMemberId: Long,
    ) {
        val board =
            boardRepository.findById(id).orElseThrow {
                BoardNotFoundException()
            }

        if (board.member.id != requestMemberId) {
            throw InvalidBoardAccessException()
        }

        boardRepository.deleteById(id)
    }

    //게시글 수정
    fun updateBoard(
        id: Long,
        boardUpdateDto: BoardRequestDto,
        requestMemberId: Long,
    ): Board {
        val board =
            boardRepository.findById(id).orElseThrow {
                BoardNotFoundException()
            }

        if (board.member.id != requestMemberId) {
            throw InvalidBoardAccessException()
        }
        boardCategoryRepository.deleteByBoardId(id)
        boardCategoryRepository.saveAll(
            categoryRepository.findByIdIn(boardUpdateDto.categoryIds).map {
                BoardCategory(
                    board = board,
                    category = Category(it.id, it.name, it.type),
                )
            },
        )
        board.updateBoard(boardUpdateDto)

        return board
    }
}
