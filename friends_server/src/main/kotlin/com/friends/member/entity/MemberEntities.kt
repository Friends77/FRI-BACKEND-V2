package com.friends.member.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.profile.entity.Profile
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member", indexes = [Index(name = "member_nickname", columnList = "nickname")])
class Member(
    // id는 불변 값으로 설정하여 JPA에서 자동으로 할당
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long = 0L,
    // IDENTITY가 1부터 시작하므로, 초기 값인 0L은 무시되며, 엔티티가 저장될 때 데이터베이스가 올바른 자동 증가 값을 할당
    var nickname: String,
    // 이메일은 중복되지 않아야 하며 수정되지 않아야 합니다.
    @Column(unique = true, updatable = false)
    val email: String,
    private var password: String? = null,
    @Enumerated(EnumType.STRING)
    var oauth2Provider: OAuth2Provider?,
    // 권한 리스트는 기본적으로 비어 있는 리스트로 초기화
    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val authorities: MutableList<Authority> = ArrayList(),
    @OneToOne(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var profile: Profile? = null,
) : BaseModifiableEntity() {
    companion object {
        /**
         * 일반 사용자 생성 메서드
         * 권한을 ROLE_USER로 초기화하여 Member 객체를 반환합니다.
         */
        fun createUser(
            nickname: String,
            email: String,
            password: String? = null,
            oauth2Provider: OAuth2Provider? = null,
        ): Member =
            Member(
                nickname = nickname,
                email = email,
                password = password,
                oauth2Provider = oauth2Provider,
            ).apply {
                addAuthority(Role.ROLE_USER) // 기본 권한을 사용자 권한으로 설정
            }

        /**
         * 관리자 생성 메서드
         * 권한을 ROLE_USER와 ROLE_ADMIN으로 초기화하여 Member 객체를 반환합니다.
         */
        fun createAdmin(
            nickname: String,
            email: String,
            password: String,
            oauth2Provider: OAuth2Provider? = null,
        ): Member =
            Member(
                nickname = nickname,
                email = email,
                password = password,
                oauth2Provider = oauth2Provider,
            ).apply {
                addAuthority(Role.ROLE_USER) // 기본 권한으로 사용자 권한 추가
                addAuthority(Role.ROLE_ADMIN) // 관리자 권한 추가
            }
    }

    /**
     * 새로운 권한을 추가하는 메서드
     * 중복된 권한을 방지하고 권한 추가 로직을 캡슐화합니다.
     */
    fun addAuthority(role: Role) {
        // 이미 동일한 권한이 존재하는지 검사 후 추가
        if (authorities.none { it.role == role }) {
            authorities.add(Authority(role = role, member = this))
        }
    }

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun getPassword(): String? = password

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }
}

@Entity
class Authority(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    val id: Long = 0L,
    @Enumerated(EnumType.STRING)
    var role: Role,
    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: Member,
) : BaseModifiableEntity()
