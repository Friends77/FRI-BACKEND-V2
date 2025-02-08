package com.friends.board.service

import com.friends.board.dto.CommentAddDto
import com.friends.board.dto.CommentUpdateDto
import com.friends.board.entity.Comment
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.exception.CommentNotFoundException
import com.friends.board.exception.InvalidCommentAccessException
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.CommentRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentCommandService(
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
) {
    //댓글 작성
    fun createComment(
        boardId: Long,
        request: CommentAddDto,
        memberId: Long,
    ): Comment {
        val member =
            memberRepository.findById(memberId).orElseThrow {
                MemberNotFoundException()
            }
        val board =
            boardRepository.findById(boardId).orElseThrow {
                BoardNotFoundException()
            }
        val savedComment =
            Comment(
                text = request.text,
                board = board,
                member = member,
            )
        return commentRepository.save(savedComment)
    }

    //댓글 삭제
    fun deleteComment(
        boardId: Long,
        commentId: Long,
        memberId: Long,
    ) {
        val comment =
            commentRepository.findByBoardIdAndId(boardId, commentId)
                ?: throw CommentNotFoundException()
        if (comment.member.id != memberId) {
            throw InvalidCommentAccessException()
        }
        commentRepository.delete(comment)
    }

    //댓글 수정
    fun updateComment(
        boardId: Long,
        commentId: Long,
        request: CommentUpdateDto,
        memberId: Long,
    ) {
        val comment =
            commentRepository.findByBoardIdAndId(boardId, commentId)
                ?: throw CommentNotFoundException()
        if (comment.member.id != memberId) {
            throw InvalidCommentAccessException()
        }
        comment.updateComment(request.text)
    }
}
