package com.friends.profile.service

import com.friends.category.repository.CategoryRepository
import com.friends.image.S3ClientService
import com.friends.image.service.ImageCommandService
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class ProfileCommandService(
    private val profileRepository: ProfileRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
    private val s3ClientService: S3ClientService,
    private val imageCommandService: ImageCommandService,
) {
    //프로필 초기 작성
    fun createProfile(
        requestMemberId: Long,
        profileCreateDto: ProfileCreateDto,
        profileImage: MultipartFile?,
    ) {
        val member =
            memberRepository
                .findById(requestMemberId)
                .orElseThrow { MemberNotFoundException() }

        val profileInterestTag = categoryRepository.findByIdIn(profileCreateDto.interestTag)
        val profile =
            Profile(
                birth = profileCreateDto.birth,
                gender = profileCreateDto.gender,
                location = profileCreateDto.location,
                selfDescription = profileCreateDto.selfDescription,
                mbti = profileCreateDto.mbti,
                imageUrl = profileImage?.let { s3ClientService.upload(it) },
                member = member,
            )
        val profileInterestTagList =
            profileInterestTag.map {
                ProfileInterestTag(
                    profile = profile,
                    category = it,
                )
            }
        profileRepository.save(profile)
        profileInterestTagRepository.saveAll(profileInterestTagList)
    }

    //프로필 수정
    @Transactional
    fun updateProfile(
        requestMemberId: Long,
        profileUpdateDto: ProfileUpdateDto,
    ) {
        val member = memberRepository.findById(requestMemberId).orElseThrow { MemberNotFoundException() }
        val profile = profileRepository.findByMemberId(requestMemberId) ?: throw ProfileNullResponseException()
        val profileImageUrl = profile.imageUrl
        imageCommandService.existsImage(profileImageUrl, profileUpdateDto.imageUrl)
        handleInterestTagUpdate(profile, profileUpdateDto)
        profile.update(profileUpdateDto)
        member.updateNickname(profileUpdateDto.nickname)
        imageCommandService.deleteOriginalImage(profileImageUrl, profileUpdateDto.imageUrl)
    }

    private fun handleInterestTagUpdate(
        profile: Profile,
        profileUpdateDto: ProfileUpdateDto,
    ) {
        profileInterestTagRepository.deleteByProfileId(profile.id)
        profileInterestTagRepository.saveAll(
            categoryRepository.findByIdIn(profileUpdateDto.interestTag).map {
                ProfileInterestTag(
                    profile = profile,
                    category = it,
                )
            },
        )
    }
}
