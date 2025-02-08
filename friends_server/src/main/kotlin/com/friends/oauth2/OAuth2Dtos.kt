package com.friends.oauth2

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuth2AccessTokenResponseDto(
    @JsonProperty("access_token")
    val accessToken: String,
)

data class UserProfileDto(
    val name: String,
    val email: String,
    val imageUrl: String,
)
