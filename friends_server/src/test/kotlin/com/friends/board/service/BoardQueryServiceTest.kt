package com.friends.board.service

import com.friends.board.BOARD_ID
import com.friends.board.INVALID_BOARD_ID
import com.friends.board.PAGEABLE
import com.friends.board.createBoardCategory
import com.friends.board.createTestBoard
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.repository.BoardCategoryRepository
import com.friends.board.repository.BoardRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import java.util.Optional

class BoardQueryServiceTest :
    BehaviorSpec({
        val boardRepository = mockk<BoardRepository>()
        val boardCategoryRepository = mockk<BoardCategoryRepository>()
        val boardQueryService = BoardQueryService(boardRepository, boardCategoryRepository)

        given("getBoard 메서드를 호출할 때") {

            val testBoard = createTestBoard()
            val testBoardCategories = createBoardCategory()

            every { boardRepository.findById(BOARD_ID) } returns Optional.of(testBoard)
            every { boardCategoryRepository.findByBoardId(BOARD_ID) } returns testBoardCategories

            `when`("존재하는 boardId가 주어졌다면") {
                val expTags = testBoardCategories.map { it.category }
                val result = boardQueryService.getBoard(BOARD_ID)

                then("Board와 Board 관련 Category 리스트를 반환해야 한다.") {
                    result.content shouldBe testBoard.content
                    result.categories!!.map { it.id } shouldBe expTags.map { it.id }
                    result.comments!!.size shouldBe testBoard.comments.size
                }
            }

            `when`("존재하지 않는 boardId가 주어졌다면") {
                every { boardRepository.findById(INVALID_BOARD_ID) } returns Optional.empty()

                then("BoardNotFoundException을 던져야 한다.") {
                    shouldThrow<BoardNotFoundException> {
                        boardQueryService.getBoard(INVALID_BOARD_ID)
                    }
                }
            }
        }

        given("getBoardList 메서드를 호출할 때") {
            val testBoard = createTestBoard()

            every { boardRepository.findAll(PAGEABLE) } returns PageImpl(listOf(testBoard))

            `when`("board의 개수가 0이 아니라면") {
//                val result = boardQueryService.getBoardList(PAGEABLE)

                then("boardList를 반환해야한다.") {
                }
            }
        }
    })
