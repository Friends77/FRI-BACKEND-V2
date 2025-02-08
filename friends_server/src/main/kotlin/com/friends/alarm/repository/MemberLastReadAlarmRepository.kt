package com.friends.alarm.repository

import com.friends.alarm.entity.MemberLastReadAlarm
import org.springframework.data.jpa.repository.JpaRepository

interface MemberLastReadAlarmRepository : JpaRepository<MemberLastReadAlarm, Long> {
    fun findByMemberId(memberId: Long): MemberLastReadAlarm?
}
