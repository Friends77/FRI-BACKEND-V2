package com.friends.friendship.service

import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.profile.createTestProfile
import com.friends.profile.entity.Profile
import com.friends.profile.repository.ProfileRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * FriendShipQueryService 통합 테스트
 */
@SpringBootTest
@Transactional
class FriendShipQueryServiceTest(
    @Autowired private val friendShipQueryService: FriendShipQueryService,
    @Autowired private val friendShipRepository: FriendShipRepository,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val profileRepository: ProfileRepository,
) : DescribeSpec(
        {
            lateinit var requester: Member
            lateinit var receiver1: Member
            lateinit var receiver2: Member
            lateinit var receiver3: Member

            lateinit var requesterProfile: Profile
            lateinit var receiver1Profile: Profile
            lateinit var receiver2Profile: Profile
            lateinit var receiver3Profile: Profile

            lateinit var friendship1: Friendship
            lateinit var friendship2: Friendship
            lateinit var friendship3: Friendship

            beforeEach {
                requester = memberRepository.save(createTestMember(email = "test1"))
                receiver1 = memberRepository.save(createTestMember(email = "test2"))
                receiver2 = memberRepository.save(createTestMember(email = "test3"))
                receiver3 = memberRepository.save(createTestMember(email = "test4"))

                requesterProfile = profileRepository.save(createTestProfile(requester))
                receiver1Profile = profileRepository.save(createTestProfile(receiver1))
                receiver2Profile = profileRepository.save(createTestProfile(receiver2))
                receiver3Profile = profileRepository.save(createTestProfile(receiver3))

                println("beforeEach")
            }
            describe("FriendShipQueryService 는") {
                context("친구 요청이 수락된 경우") {
                    it("요청이 수락된 친구만 조회한다.") {
                        friendship1 = friendShipRepository.save(Friendship(requester = requester, receiver = receiver1, friendshipStatus = FriendshipStatusEnums.WAITING))
                        friendship2 = friendShipRepository.save(Friendship(requester = requester, receiver = receiver2, friendshipStatus = FriendshipStatusEnums.ACCEPT))
                        friendship3 = friendShipRepository.save(Friendship(requester = requester, receiver = receiver3, friendshipStatus = FriendshipStatusEnums.BLOCK))

                        val result = friendShipQueryService.getFriendshipList(requester.id)

                        result.map { it.memberId } shouldContainExactly
                            listOf(
                                receiver2Profile.member.id,
                            )
                    }

                    it("요청이 여러명인 경우 이름 순으로 정렬된다.") {
                        friendship3 = friendShipRepository.save(Friendship(requester = requester, receiver = receiver3, friendshipStatus = FriendshipStatusEnums.ACCEPT))
                        friendship1 = friendShipRepository.save(Friendship(requester = requester, receiver = receiver1, friendshipStatus = FriendshipStatusEnums.ACCEPT))
                        friendship2 = friendShipRepository.save(Friendship(requester = requester, receiver = receiver2, friendshipStatus = FriendshipStatusEnums.ACCEPT))

                        val result = friendShipQueryService.getFriendshipList(requester.id)

                        result.map { it.nickname } shouldContainExactly
                            listOf(
                                receiver1Profile.member.nickname, // test1
                                receiver2Profile.member.nickname, // test2
                                receiver3Profile.member.nickname, // test3
                            )
                    }
                }
            }
        },
    )
