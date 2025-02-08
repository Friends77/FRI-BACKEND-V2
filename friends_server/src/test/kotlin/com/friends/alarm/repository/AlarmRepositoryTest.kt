package com.friends.alarm.repository

import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

@RepositoryTest
class AlarmRepositoryTest(
    private val alarmRepository: AlarmRepository,
    private val memberRepository: MemberRepository,
) : DescribeSpec({

        lateinit var sender: Member
        lateinit var receiver: Member
        lateinit var alarm1: Alarm
        lateinit var alarm2: Alarm
        lateinit var alarm3: Alarm

        beforeEach {
            // 테스트용 Member 생성
            sender = memberRepository.save(createTestMember())
            receiver = memberRepository.save(createTestMember(email = "test2"))

            // 테스트용 Alarm 생성
            alarm1 = alarmRepository.save(Alarm(sender = sender, receiver = receiver, message = "알람1", type = AlarmType.FRIEND_REQUEST))
            alarm2 = alarmRepository.save(Alarm(sender = sender, receiver = receiver, message = "알람2", type = AlarmType.FRIEND_REQUEST))
            alarm3 = alarmRepository.save(Alarm(sender = sender, receiver = receiver, message = "알람3", type = AlarmType.FRIEND_REQUEST))
        }

        describe("findAllByMemberIdBeforeId 메서드는") {

            context("알람 ID 기준으로 이전 알람을 조회할 때") {
                it("지정된 ID가 주어지지 않으면 가장 최근 알람부터 조회한다.") {
                    val result =
                        alarmRepository.findAllByMemberIdBeforeId(
                            memberId = receiver.id,
                            size = 3,
                        )

                    result.content.map { it.id } shouldContainExactly
                        listOf(
                            alarm3.id,
                            alarm2.id,
                            alarm1.id,
                        )
                }

                it("해당 ID보다 작은 알람이 없으면 빈 리스트를 반환한다.") {
                    val result =
                        alarmRepository.findAllByMemberIdBeforeId(
                            memberId = receiver.id,
                            size = 3,
                            lastAlarmId = alarm1.id,
                        )

                    result.content.size shouldBe 0
                }

                it("해당 ID 보다 작은 알람은 모두 반환한다.") {
                    val result =
                        alarmRepository.findAllByMemberIdBeforeId(
                            memberId = receiver.id,
                            size = 5,
                            lastAlarmId = alarm3.id,
                        )

                    result.content.map { it.id } shouldContainExactly
                        listOf(
                            alarm2.id,
                            alarm1.id,
                        )
                }
            }
        }
    })
