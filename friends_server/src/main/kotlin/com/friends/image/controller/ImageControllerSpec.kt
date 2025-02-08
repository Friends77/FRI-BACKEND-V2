package com.friends.image.controller

import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = "이미지 API", description = "이미지 관련 API")
interface ImageControllerSpec {
    @Operation(
        description =
            "이미지 업로드 API <br>" +
                "로그인 된 사용자만 사용 가능합니다",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "이미지 업로드 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
        ],
    )
    fun uploadImage(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestPart
        image: MultipartFile,
    ): ResponseEntity<String>
}
