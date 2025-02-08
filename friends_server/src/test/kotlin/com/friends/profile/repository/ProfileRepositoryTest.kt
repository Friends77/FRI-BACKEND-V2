package com.friends.profile.repository

import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.Profile
import com.friends.support.annotation.RepositoryTest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@RepositoryTest
class ProfileRepositoryTest
    @Autowired
    constructor(
        private val profileRepository: ProfileRepository,
        private val memberRepository: MemberRepository,
    ) {
        private lateinit var profile1: Profile
        private lateinit var profile2: Profile
        private lateinit var profile3: Profile

        @BeforeEach
        fun setup() {
            val testPoint1 = Location(10.0, 10.0)
            val testPoint2 = Location(10.1, 10.1)
            val testPoint3 = Location(20.0, 20.0)

            val member1 = memberRepository.save(Member.createUser("test1", "1test@com"))
            val member2 = memberRepository.save(Member.createUser("test2", "2test@com"))
            val member3 = memberRepository.save(Member.createUser("test3", "3test@com"))

            profile1 = profileRepository.save(Profile(member = member1, birth = LocalDate.now(), gender = GenderEnum.MAN, location = testPoint1, imageUrl = "test imageurl"))
            profile2 = profileRepository.save(Profile(member = member2, birth = LocalDate.now(), gender = GenderEnum.MAN, location = testPoint2, imageUrl = "test imageurl"))
            profile3 = profileRepository.save(Profile(member = member3, birth = LocalDate.now(), gender = GenderEnum.MAN, location = testPoint3, imageUrl = "test imageurl"))
        }

        @Test
        fun `5000 미터 이내의 TestPoint 검색`() {
            // (10,10) (10.1,10.1) 사이의 거리는 약 15,000 m 입니다
            val foundPoints = profileRepository.findAllInDistance(10.0, 10.0, 5000.0)
            for (point in foundPoints) {
                println(point)
            }
            val contains = foundPoints.map { it.id }.containsAll(listOf(profile1.id))
            val notContains = foundPoints.map { it.id }.containsAll(listOf(profile2.id))
            contains shouldBe true
            notContains shouldBe false
        }

        @Test
        fun `20000 미터 이내의 TestPoint 검색`() {
            // (10,10) (20,20) 사이의 거리는 10,000,000 m 이상입니다
            val foundPoints = profileRepository.findAllInDistance(10.0, 10.0, 20000.0)
            for (point in foundPoints) {
                println(point)
            }
            val contains = foundPoints.map { it.id }.containsAll(listOf(profile1.id, profile2.id))
            val notContains = foundPoints.map { it.id }.containsAll(listOf(profile3.id))
            contains shouldBe true
            notContains shouldBe false
        }

        @Test
        fun `20000000 미터 이내의 TestPoint 검색`() {
            val foundPoints = profileRepository.findAllInDistance(10.0, 10.0, 20000000.0)
            for (point in foundPoints) {
                println(point)
            }
            val contains = foundPoints.map { it.id }.containsAll(listOf(profile1.id, profile2.id, profile3.id))
            contains shouldBe true
        }
    }
