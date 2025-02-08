package com.friends.category

import com.friends.common.exception.ErrorCode

abstract class CategoryException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class CategoryNotFoundException : CategoryException(ErrorCode.NOT_FOUND_CATEGORY)
