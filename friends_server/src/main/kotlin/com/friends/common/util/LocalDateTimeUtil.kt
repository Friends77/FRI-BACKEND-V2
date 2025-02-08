package com.friends.common.util

import java.time.LocalDateTime
import java.time.ZoneId

object LocalDateTimeUtil {
    fun toTimeStamp(localDateTime: LocalDateTime): Long {
        return localDateTime
            .atZone(ZoneId.systemDefault()) // 시스템 시간대 적용
            .toInstant() // Instant로 변환
            .toEpochMilli() // 밀리초 기준 타임스탬프로 변환
    }
}
