package com.friends.alarm.repository

import com.friends.alarm.entity.Alarm
import com.friends.common.util.getSingle
import com.friends.common.util.getSlice
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sorts.desc
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface AlarmRepository :
    JpaRepository<Alarm, Long>,
    AlarmCustomRepository

interface AlarmCustomRepository {
    fun findAllByMemberIdBeforeId(
        memberId: Long,
        size: Int,
        lastAlarmId: Long? = null,
    ): Slice<Alarm>

    fun countByReceiverIdAndIdGreaterThan(
        receiverId: Long,
        lastAlarmId: Long?,
    ): Long
}

class AlarmCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : AlarmCustomRepository {
    override fun findAllByMemberIdBeforeId(
        memberId: Long,
        size: Int,
        lastAlarmId: Long?,
    ): Slice<Alarm> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(Alarm::class))
                .from(entity(Alarm::class))
                .where(
                    and(
                        path(Alarm::receiver).path(Member::id).equal(memberId),
                        lastAlarmId?.let { path(Alarm::id).lessThan(it) },
                    ),
                ).orderBy(desc(path(Alarm::id)))
        }
    }

    /**
     * 마지막으로 읽은 알람을 조회
     * TODO : 읽지 않은 개수의 상한치 설정 필요
     */
    override fun countByReceiverIdAndIdGreaterThan(
        receiverId: Long,
        lastAlarmId: Long?,
    ): Long =
        kotlinJdslJpqlExecutor.getSingle {
            select(count(entity(Alarm::class)))
                .from(entity(Alarm::class))
                .where(
                    and(
                        path(Alarm::receiver).path(Member::id).equal(receiverId),
                        lastAlarmId?.let { path(Alarm::id).greaterThan(it) },
                    ),
                )
        }
}
