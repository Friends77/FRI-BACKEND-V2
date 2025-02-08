package com.friends.board.exception

import com.friends.common.exception.ErrorCode

abstract class VoteException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class VoteNotFoundException : VoteException(ErrorCode.VOTE_NOT_FOUND)

class VoteOptionNotFoundException : VoteException(ErrorCode.VOTE_OPTION_NOT_FOUND)

class VoteOptionPositiveCountException : VoteException(ErrorCode.VOTE_OPTION_POSITIVE_COUNT)

class VoteNotFoundInBoardException : VoteException(ErrorCode.VOTE_NOT_FOUND_IN_BOARD)
