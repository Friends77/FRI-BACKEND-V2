package com.friends.profile.service

import com.friends.member.MEMBER_ID
import com.friends.member.MEMBER_ID_WITHOUT_PROFILE
import com.friends.member.createTestMemberWithoutProfile
import com.friends.member.repository.MemberRepository
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.createTestMember
import com.friends.profile.createTestProfile
import com.friends.profile.createTestProfileResponseDto
import com.friends.profile.repository.ProfileRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class ProfileQueryServiceTest :
    BehaviorSpec({
        val profileRepository = mockk<ProfileRepository>()
        val memberRepository = mockk<MemberRepository>()
        val profileQueryService = ProfileQueryService(profileRepository, memberRepository)

        given("getProfile 메서드를 호출할 때") {
            val testProfile = createTestProfile()

            every { memberRepository.findById(MEMBER_ID) } returns Optional.of(createTestMember())
            every { profileRepository.findByMemberId(MEMBER_ID) } returns testProfile

            `when`("해당 회원에 대한 프로필이 존재하는 경우") {
                val result = profileQueryService.getProfile(MEMBER_ID)

                then("회원의 프로필을 반환한다.") {
                    result shouldBe createTestProfileResponseDto()
                }
            }

            `when`("해당 회원에 대한 프로필이 존재하지 않는 경우") {
                every { memberRepository.findById(MEMBER_ID_WITHOUT_PROFILE) } returns Optional.of(createTestMemberWithoutProfile())
                every { profileRepository.findByMemberId(MEMBER_ID_WITHOUT_PROFILE) } returns null

                then("예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<ProfileNullResponseException> {
                            profileQueryService.getProfile(MEMBER_ID_WITHOUT_PROFILE)
                        }
                    exception.message shouldBe "해당 멤버의 프로필이 존재하지 않습니다."
                }
            }
        }
    })
