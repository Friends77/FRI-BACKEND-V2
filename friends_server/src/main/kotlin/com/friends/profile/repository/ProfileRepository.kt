package com.friends.profile.repository

import com.friends.category.entity.Category
import com.friends.common.util.getSlice
import com.friends.profile.dto.ProfileWithDistanceQueryDto
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProfileRepository :
    JpaRepository<Profile, Long>,
    ProfileCustomRepository {
    fun findByMemberId(memberId: Long): Profile?

    // Haversine 공식을 사용해서 주어진 좌표에서 주어진 거리 이내에 있는 프로필을 찾는다.
    @Query(
        """
        SELECT new com.friends.profile.dto.ProfileWithDistanceQueryDto(
            p.id, m.nickname, p.imageUrl,
            (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(p.location.latitude)) * 
            COS(RADIANS(p.location.longitude) - RADIANS(:longitude)) + 
            SIN(RADIANS(:latitude)) * SIN(RADIANS(p.location.latitude))))
        )
        FROM Profile p
        INNER JOIN Member m ON p.member.id = m.id
        WHERE (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(p.location.latitude)) * 
              COS(RADIANS(p.location.longitude) - RADIANS(:longitude)) + 
              SIN(RADIANS(:latitude)) * SIN(RADIANS(p.location.latitude)))) <= :distance
    """,
    )
    fun findAllInDistance(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("distance") distance: Double,
        pageable: Pageable? = null,
    ): List<ProfileWithDistanceQueryDto>

    @Query(
        """
        SELECT p
        FROM Profile p
        ORDER BY random()
    """,
    )
    fun findRandomProfile(pageable: Pageable): List<Profile>
}

interface ProfileCustomRepository {
    fun findProfileWithCategoryIds(
        categoryIds: List<Long>,
        pageable: Pageable,
    ): Slice<Profile>
}

class ProfileCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : ProfileCustomRepository {
    override fun findProfileWithCategoryIds(
        categoryIds: List<Long>,
        pageable: Pageable,
    ): Slice<Profile> =
        kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(Profile::class))
                .from(entity(Profile::class), innerJoin(Profile::interestTag))
                .where(path(ProfileInterestTag::category).path(Category::id).`in`(categoryIds))
                .groupBy(path(Profile::id))
                .having(count(path(ProfileInterestTag::id)).eq(categoryIds.size.toLong()))
        }
}
