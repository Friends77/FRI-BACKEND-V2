package com.friends.profile.service

import com.friends.category.repository.CategoryRepository
import com.friends.createTestCategory
import com.friends.image.InvalidImageUrlException
import com.friends.image.S3ClientService
import com.friends.image.service.ImageCommandService
import com.friends.member.MEMBER_ID
import com.friends.member.repository.MemberRepository
import com.friends.profile.PROFILE_IMAGE
import com.friends.profile.TEST_IMAGE_URL
import com.friends.profile.createTestMember
import com.friends.profile.createTestProfile
import com.friends.profile.createTestProfileCreateDto
import com.friends.profile.createTestProfileInterestTag
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import com.friends.profile.updateTestProfile
import com.friends.support.TEST_IMAGE_UPLOAD_FILE_URL
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ProfileCommandServiceTest :
    BehaviorSpec({
        val profileRepository = mockk<ProfileRepository>()
        val memberRepository = mockk<MemberRepository>()
        val categoryRepository = mockk<CategoryRepository>()
        val profileInterestTagRepository = mockk<ProfileInterestTagRepository>()
        val s3ClientService = mockk<S3ClientService>()
        val imageCommandService = ImageCommandService(memberRepository, s3ClientService)
        val profileCommandService = ProfileCommandService(profileRepository, memberRepository, categoryRepository, profileInterestTagRepository, s3ClientService, imageCommandService)

        given("createProfile 메서드를 호출할 때") {
            every { memberRepository.findById(MEMBER_ID) } returns Optional.of(createTestMember())
            every { profileRepository.save(any()) } returns createTestProfile()
            every { categoryRepository.findByIdIn(any()) } returns listOf(createTestCategory())
            every { profileInterestTagRepository.saveAll(any<List<ProfileInterestTag>>()) } returns listOf(createTestProfileInterestTag())
            every { s3ClientService.upload(any(), any()) } returns TEST_IMAGE_UPLOAD_FILE_URL
            `when`("유효한 프로필 정보를 전달하면") {
                profileCommandService.createProfile(MEMBER_ID, createTestProfileCreateDto(), PROFILE_IMAGE)

                then("프로필이 저장되어야 한다.") {
                    verify(exactly = 1) { memberRepository.findById(MEMBER_ID) }
                    verify(exactly = 1) {
                        profileRepository.save(
                            match {
                                it.birth == createTestProfileCreateDto().birth &&
                                    it.gender == createTestProfileCreateDto().gender &&
                                    it.location == createTestProfileCreateDto().location &&
                                    it.selfDescription == createTestProfileCreateDto().selfDescription &&
                                    it.mbti == createTestProfileCreateDto().mbti &&
                                    it.imageUrl == createTestProfileCreateDto().imageUrl
                            },
                        )
                    }
                }
            }
        }

        given("updateProfile 메서드를 호출할 때") {
            val existingProfile = createTestProfile(imageUrl = TEST_IMAGE_URL)
            val updatedProfile = updateTestProfile()

            every { profileRepository.findByMemberId(MEMBER_ID) } returns existingProfile
            every { profileInterestTagRepository.deleteByProfileId(any()) } returns Unit
            every { categoryRepository.findByIdIn(any<Set<Long>>()) } returns listOf(createTestCategory())
            every { profileInterestTagRepository.saveAll(any<List<ProfileInterestTag>>()) } returns listOf(createTestProfileInterestTag())
            every { s3ClientService.deleteS3Object(any()) } returns Unit
            every { memberRepository.findById(MEMBER_ID) } returns Optional.of(createTestMember())
            `when`("존재하는 프로필을 수정하면") {
                every { s3ClientService.isExist(any()) } returns true
                then("수정된 프로필이 저장되어야 한다.") {
                    profileCommandService.updateProfile(MEMBER_ID, updatedProfile)
                    verify(exactly = 1) {
                        profileRepository.findByMemberId(MEMBER_ID)
                        s3ClientService.deleteS3Object(any()) // 기존 이미지 삭제
                        s3ClientService.isExist(any())
                    }
                    existingProfile.birth shouldBe updatedProfile.birth
                    existingProfile.gender shouldBe updatedProfile.gender
                    existingProfile.location shouldBe updatedProfile.location
                    existingProfile.selfDescription shouldBe updatedProfile.selfDescription
                    existingProfile.mbti shouldBe updatedProfile.mbti
                    existingProfile.imageUrl shouldBe updatedProfile.imageUrl
                }
            }

            `when`("기존 프로필 이미지가 존재하지 않으면") {
                every { s3ClientService.isExist(any()) } returns true
                existingProfile.imageUrl = null
                then("이미지가 삭제되지 않아야 한다.") {
                    profileCommandService.updateProfile(MEMBER_ID, updatedProfile)
                    verify(exactly = 0) { s3ClientService.deleteS3Object(any()) }
                    verify(exactly = 1) { s3ClientService.isExist(any()) } // 삭제 로직 실행되지 않음
                    existingProfile.imageUrl shouldBe updatedProfile.imageUrl // 이미지가 업데이트 되었는지 확인
                }
            }

            `when`("이미지 수정이 없을 경우(updateDto의 imageUrl이 기존과 동일한 경우)") {
                then("기존 이미지가 유지되어야 한다.") {
                    profileCommandService.updateProfile(MEMBER_ID, updateTestProfile(imageUrl = TEST_IMAGE_URL))
                    verify(exactly = 0) {
                        s3ClientService.deleteS3Object(any())
                        s3ClientService.isExist(any())
                    } // 새로운 이미지 업로드/삭제 로직 실행되지 않음
                    existingProfile.imageUrl shouldBe TEST_IMAGE_URL // 이미지가 업데이트 되지 않았는지 확인
                }
            }

            `when`("updateDto의 imageUrl이 null일 경우") {
                then("이미지가 기본으로 변경되어야 한다.") {
                    profileCommandService.updateProfile(MEMBER_ID, updateTestProfile(imageUrl = null))
                    verify(exactly = 1) { s3ClientService.deleteS3Object(any()) } // 기존 이미지 삭제
                    verify(exactly = 0) { s3ClientService.isExist(any()) }
                    existingProfile.imageUrl shouldBe null
                }
            }

            `when`("updateDto의 imageUrl이 유효하지 않은 URL일 경우") {
                every { s3ClientService.isExist(any()) } returns false
                then("이미지가 삭제되지 않아야 하며 InvalidImageUrlException 에러가 발생해야 한다.") {
                    shouldThrow<InvalidImageUrlException> { profileCommandService.updateProfile(MEMBER_ID, updateTestProfile(imageUrl = "invalid url")) }
                    verify(exactly = 0) { s3ClientService.deleteS3Object(any()) }
                    verify(exactly = 1) { s3ClientService.isExist(any()) }
                }
            }
        }
    })
