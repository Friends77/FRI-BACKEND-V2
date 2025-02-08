package com.friends.jwt

import com.friends.config.AuthProperties
import com.friends.security.AtRtDto
import com.friends.security.securityException.InvalidTokenException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AtRtService(
    val jwtService: JwtService,
    val authProperties: AuthProperties,
    val authJwtRepository: AuthJwtRepository,
) {
    fun createAtRt(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): AtRtDto {
        val accessToken = createAccessToken(memberId, authorities)
        val refreshToken = createRefreshToken(memberId, authorities)
        authJwtRepository.save(accessToken, refreshToken)
        return AtRtDto(accessToken, refreshToken)
    }

    private fun createAccessToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String =
        jwtService.createToken(
            "memberId" to memberId,
            "authorities" to authorities.map { it.authority },
            expirationSeconds = authProperties.accessTokenExpiration,
        )

    private fun createRefreshToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String =
        jwtService.createToken(
            "memberId" to memberId,
            "authorities" to authorities.map { it.authority },
            expirationSeconds = authProperties.refreshTokenExpiration,
        )

    fun getMemberId(token: String): Long = jwtService.getClaim(token, "memberId", Long::class.javaObjectType) ?: throw InvalidTokenException()

    fun getAuthorities(token: String): List<GrantedAuthority> {
        val authorities: List<String> = jwtService.getClaim(token, "authorities", List::class.javaObjectType)?.filterIsInstance<String>() ?: throw InvalidTokenException()
        return authorities.map { SimpleGrantedAuthority(it) }
    }

    fun validateRefreshToken(refreshToken: String): Boolean = authJwtRepository.getAccessToken(refreshToken) != null

    fun validateAccessToken(accessToken: String): Boolean = authJwtRepository.getRefreshToken(accessToken) != null

    fun deleteAccessToken(accessToken: String) = authJwtRepository.deleteAccessToken(accessToken)

    fun deleteRefreshToken(refreshToken: String) = authJwtRepository.deleteRefreshToken(refreshToken)

    fun getAccessToken(refreshToken: String): String? = authJwtRepository.getAccessToken(refreshToken)

    fun getRefreshToken(accessToken: String): String? = authJwtRepository.getRefreshToken(accessToken)

    fun getExpiration(token: String): Date = jwtService.getExpiration(token)
}
