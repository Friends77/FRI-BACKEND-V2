package com.friends.alarm.controller

import com.friends.alarm.AlarmResponseDto
import com.friends.alarm.service.AlarmQueryService
import com.friends.common.dto.SliceBaseResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/alarm")
class AlarmController(
    private val alarmQueryService: AlarmQueryService,
) : AlarmControllerSpec {
    @GetMapping
    override fun getAlarmList(
        @AuthenticationPrincipal memberId: Long,
        @RequestParam(required = false, defaultValue = "20") size: Int,
        @RequestParam(required = false) lastAlarmId: Long?,
    ): ResponseEntity<SliceBaseResponse<AlarmResponseDto>> {
        val result = alarmQueryService.getAlarmList(memberId, size, lastAlarmId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/unread-count")
    override fun getUnreadAlarmCount(
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Long> {
        val result = alarmQueryService.getUnreadAlarmCount(memberId)
        return ResponseEntity.ok(result)
    }
}
