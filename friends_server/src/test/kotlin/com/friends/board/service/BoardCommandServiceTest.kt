package com.friends.board.service

import com.friends.board.INVALID_BOARD_ID
import com.friends.board.NON_AUTHORIZED_MEMBER_ID
import com.friends.board.REQUEST_MEMBER_ID
import com.friends.board.createBoardCategory
import com.friends.board.createBoardRequestDto
import com.friends.board.createTestBoard
import com.friends.board.createTestMember
import com.friends.board.entity.BoardCategory
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.exception.InvalidBoardAccessException
import com.friends.board.repository.BoardCategoryRepository
import com.friends.board.repository.BoardRepository
import com.friends.category.repository.CategoryRepository
import com.friends.createTestCategory
import com.friends.member.repository.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class BoardCommandServiceTest :
    BehaviorSpec({
        val boardRepository = mockk<BoardRepository>()
        val memberRepository = mockk<MemberRepository>()
        val categoryRepository = mockk<CategoryRepository>()
        val boardCategoryRepository = mockk<BoardCategoryRepository>()

        val boardCommandService =
            BoardCommandService(
                boardRepository = boardRepository,
                memberRepository = memberRepository,
                categoryRepository = categoryRepository,
                boardCategoryRepository = boardCategoryRepository,
            )

        given("createBoard 메서드를 호출할 때") {
            every { memberRepository.findById(REQUEST_MEMBER_ID) } returns Optional.of(createTestMember())
            every { boardRepository.save(any()) } answers { firstArg() }
            every { categoryRepository.findByName(any()) } answers { null } //모든 해시태그는 새로 생성됩니다.
            every { categoryRepository.save(any()) } answers { firstArg() } //저장된 해시태그 반환
            every { boardCategoryRepository.saveAll(any<List<BoardCategory>>()) } answers { firstArg() }
            every { categoryRepository.findByIdIn(any()) } answers { listOf(createTestCategory()) }

            val testMember = createTestMember()

            `when`("유효한 작성자가 글을 작성했으면") {
                val result = boardCommandService.createBoard(createBoardRequestDto, REQUEST_MEMBER_ID)

                then("게시글이 저장되고 반환되어야 한다.") {
                    result.content shouldBe createBoardRequestDto.content
                    result.member === testMember
                }
            }
        }

        given("deleteBoard 메서드를 호출할 때") {
            every { boardRepository.findById(createTestBoard().id) } returns Optional.of(createTestBoard())
            every { boardRepository.deleteById(createTestBoard().id) } returns Unit

            `when`("삭제하려는 회원이 해당 게시글의 작성자라면") {
                boardCommandService.deleteBoard(createTestBoard().id, REQUEST_MEMBER_ID)

                then("게시글이 삭제되어야 한다.") {
                    io.mockk.verify { boardRepository.deleteById(createTestBoard().id) }
                }
            }

            `when`("삭제하려는 회원이 해당 게시글의 작성자가 아니라면") {

                then("권한 없음 예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<InvalidBoardAccessException> {
                            boardCommandService.deleteBoard(createTestBoard().id, NON_AUTHORIZED_MEMBER_ID)
                        }
                    exception.message shouldBe "게시글에 대한 유효하지 않은 접근입니다."
                }
            }

            `when`("boardId가 존재하지 않다면") {
                every { boardRepository.findById(any()) } returns Optional.empty()

                then("예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<BoardNotFoundException> {
                            boardCommandService.deleteBoard(INVALID_BOARD_ID, REQUEST_MEMBER_ID)
                        }
                    exception.message shouldBe "존재하지 않는 게시물입니다."
                }
            }
        }

        given("updateBoard 메서드를 호출할 때") {
            every { boardRepository.findById(createTestBoard().id) } returns Optional.of(createTestBoard())
            every { boardRepository.deleteById(createTestBoard().id) } returns Unit
            every { boardCategoryRepository.deleteByBoardId(any()) } returns Unit
            every { categoryRepository.findByIdIn(any()) } answers { listOf(createTestCategory()) }

            every { boardCategoryRepository.saveAll(any<List<BoardCategory>>()) } returns createBoardCategory()

            `when`("수정하려는 회원이 해당 게시글의 작성자라면") {
                val updatedContent = "Updated content"
                val updatedDto = createBoardRequestDto.copy(content = updatedContent)
                createTestBoard().content = updatedContent

                val result = boardCommandService.updateBoard(createTestBoard().id, updatedDto, REQUEST_MEMBER_ID)

                then("게시글의 내용이 수정되어야 한다.") {
                    result.content shouldBe updatedContent
                }
            }

            `when`("수정하려는 회원이 해당 게시글의 작성자가 아니라면") {

                then("권한 없음 예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<InvalidBoardAccessException> {
                            boardCommandService.updateBoard(createTestBoard().id, createBoardRequestDto, NON_AUTHORIZED_MEMBER_ID)
                        }
                    exception.message shouldBe "게시글에 대한 유효하지 않은 접근입니다."
                }
            }
        }
    })
