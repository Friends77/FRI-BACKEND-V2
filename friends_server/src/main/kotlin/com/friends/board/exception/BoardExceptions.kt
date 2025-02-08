package com.friends.board.exception

import com.friends.common.exception.ErrorCode

abstract class BoardException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class BoardNotFoundException : BoardException(ErrorCode.BOARD_NOT_FOUND)

class InvalidBoardAccessException : BoardException(ErrorCode.INVALID_BOARD_ACCESS)

class BoardLikeAlreadyExists : BoardException(ErrorCode.BOARD_LIKE_ALREADY_EXISTS)

class BoardLikeNotFoundException : BoardException(ErrorCode.BOARD_LIKE_NOT_FOUND)

class NotFoundBoardCategoryException : BoardException(ErrorCode.BOARD_CATEGORY_NOT_FOUND)
