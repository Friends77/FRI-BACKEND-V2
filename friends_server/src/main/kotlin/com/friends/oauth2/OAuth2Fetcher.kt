package com.friends.oauth2

import com.friends.config.OAuth2Properties
import com.friends.member.entity.OAuth2Provider
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.nio.charset.StandardCharsets

@Component
class OAuth2Fetcher(
    private val oAuth2Properties: OAuth2Properties,
) {
    fun getAccessToken(
        code: String,
        oAuth2Provider: OAuth2Provider,
    ): OAuth2AccessTokenResponseDto {
        val oAuth2Property = oAuth2Properties.get(oAuth2Provider)
        return try {
            WebClient
                .create()
                .post()
                .uri(oAuth2Property.tokenUrl)
                .headers {
                    it.setBasicAuth(oAuth2Property.clientId, oAuth2Property.clientSecret)
                    it.contentType = MediaType.APPLICATION_FORM_URLENCODED
                    it.accept = listOf(MediaType.APPLICATION_JSON)
                    it.acceptCharset = listOf(StandardCharsets.UTF_8)
                }.bodyValue(accessTokenRequestForm(code, oAuth2Provider))
                .retrieve()
                .bodyToMono(OAuth2AccessTokenResponseDto::class.java)
                .block() ?: throw OAuth2NullResponseException()
        } catch (e: Exception) {
            throw OAuth2AccessTokenFetchFailedException()
        }
    }

    fun getUserAttributes(
        accessToken: String,
        oAuth2Provider: OAuth2Provider,
    ): Map<String, Any> {
        val oAuth2Property = oAuth2Properties.get(oAuth2Provider)
        return try {
            WebClient
                .create()
                .post()
                .uri(oAuth2Property.userInfoUrl)
                .headers { headers ->
                    headers.setBearerAuth(accessToken)
                }.retrieve()
                .bodyToMono<Map<String, Any>>()
                .block() ?: throw OAuth2NullResponseException()
        } catch (e: Exception) {
            throw OAuth2UserInfoFetchFailedException()
        }
    }

    private fun accessTokenRequestForm(
        code: String,
        oAuth2Provider: OAuth2Provider,
    ): MultiValueMap<String, String> {
        val form = LinkedMultiValueMap<String, String>()
        val oAuth2Property = oAuth2Properties.get(oAuth2Provider)
        form.add("code", code)
        form.add("grant_type", "authorization_code")
        form.add("redirect_uri", oAuth2Property.redirectUrl)
        return form
    }
}
