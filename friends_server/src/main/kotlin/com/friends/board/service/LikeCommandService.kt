package com.friends.board.service

import com.friends.board.dto.LikeRequestDto
import com.friends.board.entity.Like
import com.friends.board.exception.BoardLikeAlreadyExists
import com.friends.board.exception.BoardLikeNotFoundException
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.LikeRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeCommandService(
    private val likeRepository: LikeRepository,
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
) {
    //좋아요 누르기
    @Transactional
    fun createLike(
        likeRequestDto: LikeRequestDto,
    ) {
        val member =
            memberRepository.findById(likeRequestDto.member.id)
                .orElseThrow {
                    MemberNotFoundException()
                }
        val board =
            boardRepository.findById(likeRequestDto.board.id)
                .orElseThrow {
                    BoardNotFoundException()
                }
        if (likeRepository.findByMemberAndBoard(member, board) != null) {
            throw BoardLikeAlreadyExists()
        }
        val like =
            Like(
                member = member,
                board = board,
            )
        likeRepository.save(like)
        board.increaseLike()
    }

    //좋아요 삭제
    @Transactional
    fun deleteLike(
        likeRequestDto: LikeRequestDto,
    ) {
        val member =
            memberRepository.findById(likeRequestDto.member.id)
                .orElseThrow {
                    MemberNotFoundException()
                }
        val board =
            boardRepository.findById(likeRequestDto.board.id)
                .orElseThrow {
                    BoardNotFoundException()
                }
        val like =
            likeRepository.findByMemberAndBoard(member, board)
                ?: throw BoardLikeNotFoundException()
        likeRepository.delete(like)
        board.decreaseLike()
    }
}
