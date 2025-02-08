package com.friends.jwt

import com.friends.config.AuthProperties
import com.friends.member.MEMBER_ID
import com.friends.member.makeUserAuthorities
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AtRtServiceTest :
    BehaviorSpec({
        val jwtService = mockk<JwtService>()
        val authProperties =
            AuthProperties(
                accessTokenExpiration = 3600L,
                refreshTokenExpiration = 7200L,
                0,
                0,
                0,
            )
        val authJwtRepository = mockk<AuthJwtRepository>(relaxed = true)
        val atRtService = AtRtService(jwtService, authProperties, authJwtRepository)

        given("createAtRt 호출") {
            val memberId = MEMBER_ID
            val authorities = makeUserAuthorities()
            val accessToken = VALID_ACCESS_TOKEN
            val refreshToken = VALID_REFRESH_TOKEN

            every {
                jwtService.createToken(
                    "memberId" to memberId,
                    "authorities" to authorities.map { it.authority },
                    expirationSeconds = authProperties.accessTokenExpiration,
                )
            } returns accessToken

            every {
                jwtService.createToken(
                    "memberId" to memberId,
                    "authorities" to authorities.map { it.authority },
                    expirationSeconds = authProperties.refreshTokenExpiration,
                )
            } returns refreshToken

            `when`("createAtRt를 호출하면") {
                val atRtDto = atRtService.createAtRt(memberId, authorities)

                then("AccessToken과 RefreshToken이 생성되어야 한다") {
                    atRtDto.accessToken shouldBe accessToken
                    atRtDto.refreshToken shouldBe refreshToken
                }

                then("AuthJwtRepository에 토큰이 저장되어야 한다") {
                    verify { authJwtRepository.save(accessToken, refreshToken) }
                }
            }
        }

        given("getMemberId 호출") {
            val token = "mockToken"
            val memberId = 123L

            every { jwtService.getClaim(token, "memberId", Long::class.javaObjectType) } returns memberId

            `when`("getMemberId를 호출하면") {
                val result = atRtService.getMemberId(token)

                then("올바른 memberId를 반환해야 한다") {
                    result shouldBe memberId
                }
            }
        }

        given("getAuthorities 호출") {
            val token = VALID_TOKEN
            val authorities = listOf("ROLE_USER", "ROLE_ADMIN")

            every { jwtService.getClaim(token, "authorities", List::class.javaObjectType) } returns authorities

            `when`("getAuthorities를 호출하면") {
                val result = atRtService.getAuthorities(token)

                then("GrantedAuthority 리스트를 반환해야 한다") {
                    result shouldBe authorities.map { SimpleGrantedAuthority(it) }
                }
            }
        }

        given("토큰 검증 메서드 호출") {
            val accessToken = VALID_ACCESS_TOKEN
            val refreshToken = VALID_REFRESH_TOKEN

            every { authJwtRepository.getAccessToken(refreshToken) } returns accessToken
            every { authJwtRepository.getRefreshToken(accessToken) } returns refreshToken

            `when`("validateRefreshToken이 호출되면") {
                val result = atRtService.validateRefreshToken(refreshToken)

                then("refreshToken이 유효해야 한다") {
                    result shouldBe true
                }
            }

            `when`("validateAccessToken이 호출되면") {
                val result = atRtService.validateAccessToken(accessToken)

                then("accessToken이 유효해야 한다") {
                    result shouldBe true
                }
            }
        }

        given("토큰 삭제 메서드 호출") {
            val accessToken = VALID_ACCESS_TOKEN
            val refreshToken = VALID_REFRESH_TOKEN

            every { authJwtRepository.deleteAccessToken(accessToken) } returns true
            every { authJwtRepository.deleteRefreshToken(refreshToken) } returns true

            `when`("deleteAccessToken이 호출되면") {
                atRtService.deleteAccessToken(accessToken)

                then("accessToken이 삭제되어야 한다") {
                    verify { authJwtRepository.deleteAccessToken(accessToken) }
                }
            }

            `when`("deleteRefreshToken이 호출되면") {
                atRtService.deleteRefreshToken(refreshToken)

                then("refreshToken이 삭제되어야 한다") {
                    verify { authJwtRepository.deleteRefreshToken(refreshToken) }
                }
            }
        }
    })
