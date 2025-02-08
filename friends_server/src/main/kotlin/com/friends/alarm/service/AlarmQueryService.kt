package com.friends.alarm.service

import com.friends.alarm.AlarmResponseDto
import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.MemberLastReadAlarm
import com.friends.alarm.repository.AlarmRepository
import com.friends.alarm.repository.MemberLastReadAlarmRepository
import com.friends.alarm.toAlarmResponseDto
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AlarmQueryService(
    private val memberRepository: MemberRepository,
    private val alarmRepository: AlarmRepository,
    private val memberLastReadAlarmRepository: MemberLastReadAlarmRepository,
) {
    @Transactional // 읽지 않은 알람 개수를 조회하기 위해 읽은 알람을 저장하기 때문에 Transactional 필요
    fun getAlarmList(
        memberId: Long,
        size: Int,
        lastAlarmId: Long?,
    ): SliceBaseResponse<AlarmResponseDto> {
        val result = alarmRepository.findAllByMemberIdBeforeId(memberId, size, lastAlarmId)

        // 마지막으로 읽은 알람을 저장 (읽지 않은 알람 개수를 알기 위함)
        if (!result.isEmpty) {
            val lastReadAlarm = result.content.first()
            saveLastReadAlarm(memberId, lastReadAlarm)
        }

        return toSliceBaseResponse(result.map { toAlarmResponseDto(it) })
    }

    // 마지막으로 읽은 알람을 저장 (읽지 않은 알람 개수를 알기 위함)
    private fun saveLastReadAlarm(
        memberId: Long,
        lastReadAlarm: Alarm,
    ) {
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        memberLastReadAlarmRepository.save(
            MemberLastReadAlarm(
                member = member,
                alarm = lastReadAlarm,
            ),
        )
    }

    fun getUnreadAlarmCount(memberId: Long): Long {
        // 마지막으로 읽은 알람을 조회
        val lastReadAlarm = memberLastReadAlarmRepository.findByMemberId(memberId)
        return alarmRepository.countByReceiverIdAndIdGreaterThan(memberId, lastReadAlarm?.alarm?.id)
    }
}
