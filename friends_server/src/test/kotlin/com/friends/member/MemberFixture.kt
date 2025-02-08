package com.friends.member

import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.entity.Profile
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.util.ReflectionTestUtils

const val MEMBER_NICKNAME = "test"
const val MEMBER_OTHER_NICKNAME = "test2"
const val MEMBER_EMAIL = "test@gmail.com"
const val MEMBER_OTHER_EMAIL = "test2@naver.com"
const val MEMBER_PASSWORD = "test1234"
val MEMBER_ID = 123L
val MEMBER_ID_WITHOUT_PROFILE = 123L

fun makeUserAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
    )

fun makeAdminAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
        SimpleGrantedAuthority("ROLE_ADMIN"),
    )

fun createTestMember(
    nickname: String = MEMBER_NICKNAME,
    email: String = MEMBER_EMAIL,
    password: String = MEMBER_PASSWORD,
    oauth2Provider: OAuth2Provider? = null,
    profile: Profile? = null,
) = Member.createUser(nickname, email, password, oauth2Provider).apply { ReflectionTestUtils.setField(this, "profile", profile) }

fun createTestMemberWithId(
    id: Long = MEMBER_ID,
    nickname: String = MEMBER_NICKNAME,
    email: String = MEMBER_EMAIL,
    password: String = MEMBER_PASSWORD,
    oauth2Provider: OAuth2Provider? = null,
    profile: Profile? = null,
) = Member(id = id, nickname = nickname, email = email, password = password, oauth2Provider = oauth2Provider).apply { ReflectionTestUtils.setField(this, "profile", profile) }

fun createTestMemberWithoutProfile(): Member = Member(id = MEMBER_ID_WITHOUT_PROFILE, nickname = "test name2", email = "test@test2.com", password = "12345", oauth2Provider = OAuth2Provider.NAVER)
