package com.friends.image

import com.friends.common.exception.ErrorCode

abstract class ImageException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class InvalidImageUrlException : ImageException(ErrorCode.INVALID_IMAGE_URL)
