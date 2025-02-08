package com.friends.jwt

import com.friends.config.JwtProperties
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import java.util.Date
import kotlin.math.abs

class JwtServiceTest :
    BehaviorSpec({
        val jwtSecret = "sample-secret-key-1234123412341234123412341234123412341234"
        val jwtProperties = JwtProperties(secretKey = jwtSecret)
        val jwtService = JwtService(jwtProperties)

        given("유효한 jwtService 가 주어졌을 때") {
            val claims = mapOf("number" to "12345", "array" to listOf(1, 2, 3))
            val expirationSeconds = 3600L

            `when`("토큰이 적잘한 클레임과 만료 시간을 가지고 생성될 때") {
                val token =
                    jwtService.createToken(
                        "number" to claims["number"]!!,
                        "array" to claims["array"]!!,
                        expirationSeconds = expirationSeconds,
                    )

                then("생성된 토큰은 비어있지 않아야 한다.") {
                    token.shouldNotBeEmpty()
                }

                then("토큰은 주어진 클레임을 가지고 있어야 한다.") {
                    val number = jwtService.getClaim(token, "number", String::class.java)
                    val array = jwtService.getClaim(token, "array", List::class.java)

                    number shouldBe claims["number"]
                    array shouldBe claims["array"]
                }

                then("토큰은 주어진 만료 시간을 가지고 있어야 한다.") {
                    val expiration = jwtService.getExpiration(token)
                    val expectedExpiration = Date(System.currentTimeMillis() + expirationSeconds * 1000)

                    abs(expiration.time - expectedExpiration.time) shouldBeLessThan 2000
                }
            }

            `when`("적절한 유효성 검사가 수행될 때") {
                val token =
                    jwtService.createToken(
                        "number" to claims["number"]!!,
                        "array" to claims["array"]!!,
                        expirationSeconds = expirationSeconds,
                    )

                then("유효한 토큰에 대해 검사가 성공해야 한다.") {
                    jwtService.validate(token) shouldBe true
                }

                then("유효하지 않은 토큰에 대해 검사가 실패해야 한다.") {
                    jwtService.validate("invalid.token.string") shouldBe false
                }
            }
            `when`("만료된 토큰이 주어졌을 때") {
                val expiredToken =
                    jwtService.createToken(
                        "number" to claims["number"]!!,
                        "array" to claims["array"]!!,
                        expirationSeconds = -1,
                    )

                then("만료된 토큰에 대해 검사가 실패해야 한다.") {
                    jwtService.validate(expiredToken) shouldBe false
                }
            }
        }
    })
