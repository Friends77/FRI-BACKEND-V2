package com.friends.board.exception

import com.friends.common.exception.ErrorCode

abstract class CommentException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class CommentNotFoundException : CommentException(ErrorCode.NOT_FOUND_COMMENT)

class InvalidCommentAccessException : CommentException(ErrorCode.INVALID_COMMENT_ACCESS)
