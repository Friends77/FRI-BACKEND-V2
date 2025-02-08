package com.friends.alarm.controller

import com.friends.alarm.AlarmResponseDto
import com.friends.common.dto.SliceBaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Alarm", description = "알람 API")
interface AlarmControllerSpec {
    @Operation(
        description =
            "알람 조회 API <br>" +
                "API 호출 후 사용자의 모든 알림은 읽음 처리 됩니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "알람 조회 성공",
            ),
        ],
    )
    fun getAlarmList(
        memberId: Long,
        size: Int,
        lastAlarmId: Long?,
    ): ResponseEntity<SliceBaseResponse<AlarmResponseDto>>

    @Operation(
        description = "알람 읽지 않은 개수 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "알람 읽지 않은 개수 조회 성공",
            ),
        ],
    )
    fun getUnreadAlarmCount(memberId: Long): ResponseEntity<Long>
}
