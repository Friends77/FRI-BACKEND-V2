package com.friends.admin

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Admin", description = "관리자 API(백엔드 작업시 필요한 API)")
interface AdminControllerSpec {
    @Operation(
        summary = "기본 이미지 업로드",
        description = "기존 기본 이미지를 삭제하고 새로운 기본 이미지를 업로드합니다.",
    )
    fun uploadImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("type") type: BaseImageType,
    ): ResponseEntity<String>

    @Operation(
        summary = "이미지 삭제",
        description = "이미지를 삭제합니다.",
    )
    fun deleteImage(
        @Schema(description = "이미지 URL", example = "https://friends-image.s3.ap-northeast-2.amazonaws.com/12343-1234")
        url: String,
    ): ResponseEntity<Void>

    @Operation(
        summary = "채팅방 추천 기준 집계",
        description = "채팅방 추천 기준(가장 많은 연령대, 성별)을 즉시 집계합니다.",
    )
    fun aggregateChatRoomRecommendationCriteria(): ResponseEntity<Void>
}
