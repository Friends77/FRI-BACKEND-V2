package com.friends.secondaryToken

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/user")
@RestController
class SecondaryTokenController(
    private val secondaryTokenService: SecondaryTokenService,
) : SecondaryTokenControllerSpec {
    @GetMapping("/secondaryToken")
    override fun getSecondaryToken(
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<SecondaryTokenResponseDto> {
        val secondaryToken = secondaryTokenService.createAndSaveSecondaryToken(memberId)
        return ResponseEntity.ok(SecondaryTokenResponseDto(secondaryToken))
    }
}
