package com.friends.profile.repository

import com.friends.profile.entity.ProfileInterestTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileInterestTagRepository : JpaRepository<ProfileInterestTag, Long> {
    fun deleteByProfileId(profileId: Long)
}
