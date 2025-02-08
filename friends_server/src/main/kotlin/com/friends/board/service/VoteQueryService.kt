package com.friends.board.service

import com.friends.board.dto.VoteOptionResponseDto
import com.friends.board.dto.VoteResponseDto
import com.friends.board.exception.VoteNotFoundInBoardException
import com.friends.board.repository.VoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteQueryService(
    private val voteRepository: VoteRepository,
) {
    /**
     * 투표글 조회하기
     */
    @Transactional(readOnly = true)
    fun getVoteByBoardId(boardId: Long): VoteResponseDto {
        val vote =
            voteRepository.findByBoardId(boardId)
                ?: throw VoteNotFoundInBoardException()
        return VoteResponseDto(
            voteId = vote.id,
            boardId = vote.board.id,
            options =
                vote.options.map { option ->
                    VoteOptionResponseDto(
                        optionId = option.id,
                        content = option.content,
                        voteCount = option.voteCount,
                    )
                },
        )
    }
}
