package com.friends.profile.entity

import com.friends.category.entity.Category
import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import com.friends.profile.dto.ProfileUpdateDto
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.time.LocalDate

@Entity
class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    val id: Long = 0L,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    var birth: LocalDate,
    @Enumerated(EnumType.STRING)
    var gender: GenderEnum,
    @Embedded
    var location: Location? = null,
    @Column(name = "self_description", length = 100)
    var selfDescription: String? = null,
    @Enumerated(EnumType.STRING)
    var mbti: MbtiEnum? = null,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "profile")
    @Column(name = "Category", length = 225)
    var interestTag: MutableSet<ProfileInterestTag> = mutableSetOf(),
    @Column(name = "image_url")
    var imageUrl: String? = null,
) : BaseModifiableEntity() {
    fun update(
        profileUpdateDto: ProfileUpdateDto,
    ) {
        this.birth = profileUpdateDto.birth
        this.gender = profileUpdateDto.gender
        this.location = profileUpdateDto.location
        this.selfDescription = profileUpdateDto.selfDescription
        this.mbti = profileUpdateDto.mbti
        this.imageUrl = profileUpdateDto.imageUrl
    }
}

@Embeddable
data class Location(
    val latitude: Double,
    val longitude: Double,
)

@Entity
class ProfileInterestTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_interest_tag_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    var profile: Profile,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
)
