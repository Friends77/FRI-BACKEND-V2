package com.friends.config

import com.friends.member.entity.OAuth2Provider
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth")
class AuthProperties(
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
    val emailCodeExpiration: Long,
    val emailJwtExpiration: Long,
    val oauth2JwtExpiration: Long,
)

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secretKey: String,
)

@ConfigurationProperties(prefix = "spring.mail")
class EmailProperties(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val auth: Boolean,
    val starttls: Boolean,
    val debug: Boolean,
    val connectiontimeout: Int,
)

@ConfigurationProperties(prefix = "oauth2")
class OAuth2Properties(
    val naver: Provider,
    val google: Provider,
) {
    fun get(oAuth2Provider: OAuth2Provider): Provider =
        when (oAuth2Provider) {
            OAuth2Provider.NAVER -> naver
            OAuth2Provider.GOOGLE -> google
        }

    data class Provider(
        val clientId: String,
        val clientSecret: String,
        val redirectUrl: String,
        val tokenUrl: String,
        val userInfoUrl: String,
    )
}
