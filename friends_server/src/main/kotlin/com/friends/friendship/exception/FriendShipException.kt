package com.friends.friendship.exception

import com.friends.common.exception.ErrorCode

abstract class FriendShipException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class FriendShipAlreadyExistException : FriendShipException(ErrorCode.FRIENDSHIP_ALREADY_EXIST)

class FriendShipNotFoundException : FriendShipException(ErrorCode.FRIENDSHIP_NOT_FOUND)

class FriendShipNotWaitingException : FriendShipException(ErrorCode.FRIENDSHIP_NOT_WAITING)

class FriendShipBlockedException : FriendShipException(ErrorCode.FRIENDSHIP_BLOCKED)
