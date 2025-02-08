package com.friends.jwt

import com.friends.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * JWT 설정 정보를 담고 있는 클래스입니다.
 * application.yml 파일의 jwt 설정을 바탕으로 생성됩니다.
 */

@Component
class JwtService(
    val jwtProperties: JwtProperties,
) {
    val secretKey: SecretKey by lazy { Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray()) }

    private fun getDateAfterSeconds(seconds: Long) = Date(System.currentTimeMillis() + seconds * 1000)

    fun createToken(
        vararg claims: Pair<String, Any>,
        expirationSeconds: Long,
    ): String {
        val claimsMap = mapOf(*claims)

        return Jwts
            .builder()
            .apply {
                claimsMap.forEach { (key, value) -> claim(key, value) }
            }.signWith(secretKey)
            .expiration(getDateAfterSeconds(expirationSeconds))
            .compact()
    }

    fun <T> getClaim(
        token: String,
        key: String,
        type: Class<T>,
    ): T? =
        Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .get(key, type)

    fun getExpiration(token: String): Date =
        Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload.expiration

    fun validate(token: String): Boolean =
        try {
            Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
}
