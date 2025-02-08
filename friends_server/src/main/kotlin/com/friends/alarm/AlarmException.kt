package com.friends.alarm

import com.friends.common.exception.ErrorCode

abstract class AlarmException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class AlarmNotFoundException : AlarmException(ErrorCode.ALARM_NOT_FOUND)
