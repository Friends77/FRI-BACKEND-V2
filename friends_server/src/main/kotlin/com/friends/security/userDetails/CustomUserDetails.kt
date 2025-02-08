package com.friends.security.userDetails

import com.friends.member.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val member: Member,
) : UserDetails {
    private val authorities: Collection<GrantedAuthority> =
        member.authorities.map { authority ->
            SimpleGrantedAuthority(authority.role.name)
        }

    val memberId = member.id

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String? = member.getPassword()

    override fun getUsername(): String = member.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
