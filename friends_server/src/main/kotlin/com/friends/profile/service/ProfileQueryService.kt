package com.friends.profile.service

import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileQueryService(
    val profileRepository: ProfileRepository,
    val memberRepository: MemberRepository,
) {
    @Value("\${image.profile-base-url}")
    lateinit var profileBaseImageUrl: String

    //프로필 상세조회
    @Transactional(readOnly = true)
    fun getProfile(requestMemberId: Long): ProfileResponseDto {
        val member =
            memberRepository
                .findById(requestMemberId)
                .orElseThrow { MemberNotFoundException() }

        val profile =
            profileRepository.findByMemberId(requestMemberId)
                ?: throw ProfileNullResponseException()

        return ProfileResponseDto(
            memberId = member.id,
            nickname = member.nickname,
            email = member.email,
            birth = profile.birth,
            gender = profile.gender,
            location = profile.location,
            selfDescription = profile.selfDescription,
            mbti = profile.mbti,
            interestTag = profile.interestTag.map { it.category },
            imageUrl = profile.imageUrl ?: profileBaseImageUrl,
        )
    }
}
