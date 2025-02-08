package com.friends.secondaryToken

import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Secondary Token")
interface SecondaryTokenControllerSpec {
    @Operation(
        description = "Secondary Token 발급 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Secondary Token 발급 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.UNAUTHORIZED,
        ],
    )
    fun getSecondaryToken(memberId: Long): ResponseEntity<SecondaryTokenResponseDto>
}
