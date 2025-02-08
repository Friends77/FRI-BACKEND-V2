package com.friends.board

import com.friends.TEST_CATEGORY_ID
import com.friends.board.dto.BoardRequestDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardCategory
import com.friends.createTestCategory
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

val REQUEST_MEMBER_ID = 1L
val NON_AUTHORIZED_MEMBER_ID = 2L
val BOARD_ID = 1L
val INVALID_BOARD_ID = 99L
val BOARD_HASHTAG_ID = 1L
val PAGEABLE = PageRequest.of(0, 10, Sort.by("id").ascending())

fun createTestMember(): Member = Member(id = REQUEST_MEMBER_ID, nickname = "Test Name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE)

fun createTestBoard(): Board = Board(id = BOARD_ID, member = createTestMember(), content = "Test content")

val createBoardRequestDto = BoardRequestDto(content = createTestBoard().content, categoryIds = setOf(TEST_CATEGORY_ID))

fun createBoardCategory(): List<BoardCategory> = listOf(BoardCategory(id = BOARD_HASHTAG_ID, board = createTestBoard(), category = createTestCategory()))
