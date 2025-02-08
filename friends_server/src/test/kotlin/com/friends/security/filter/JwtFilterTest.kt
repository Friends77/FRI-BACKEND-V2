package com.friends.security.filter

import com.friends.jwt.AUTHORIZATION_HEADER
import com.friends.jwt.INVALID_TOKEN
import com.friends.jwt.JwtService
import com.friends.jwt.VALID_TOKEN
import com.friends.security.authentication.AuthenticationCreator
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class JwtFilterTest :
    BehaviorSpec({
        val authenticationCreator = mockk<AuthenticationCreator>()
        val jwtService = mockk<JwtService>()
        val jwtFilter = JwtFilter(authenticationCreator, jwtService)

        /**
         * Type이 Test인 테스트(여기서는 then) 이 끝난 뒤 실행됩니다.
         * SecurityContextHolder 는 스레드 로컬 변수를 사용하기 때문에, 테스트가 끝난 뒤에는 반드시 클리어해주어야 다른 테스트에 영향을 주지 않습니다.
         */
        afterEach {
            SecurityContextHolder.clearContext()
        }

        given("HTTP 요청에 유효한 JWT가 있는 경우") {
            val token = VALID_TOKEN
            val authentication = mockk<Authentication>()
            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer $token")
                }
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            every { authenticationCreator.createByAccessToken(token) } returns authentication
            every { jwtService.validate(token) } returns true

            `when`("JwtFilter가 실행될 때") {
                jwtFilter.doFilter(request, response, filterChain)

                then("AuthenticationCreator가 호출되어야 한다") {
                    verify { authenticationCreator.createByAccessToken(token) }
                }

                then("SecurityContextHolder에 Authentication이 설정되어야 한다") {
                    SecurityContextHolder.getContext().authentication shouldBe authentication
                }

                then("필터 체인이 계속 실행되어야 한다") {
                    verify { filterChain.doFilter(request, response) }
                }
            }
        }

        given("HTTP 요청에 Authorization 헤더가 없는 경우") {
            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            `when`("JwtFilter가 실행될 때") {
                jwtFilter.doFilter(request, response, filterChain)

                then("AuthenticationCreator는 호출되지 않아야 한다") {
                    verify(exactly = 0) { authenticationCreator.createByAccessToken(any()) }
                }

                then("SecurityContextHolder는 변경되지 않아야 한다") {
                    SecurityContextHolder.getContext().authentication shouldBe null
                }

                then("필터 체인이 계속 실행되어야 한다") {
                    verify { filterChain.doFilter(request, response) }
                }
            }
        }

        given("HTTP 요청에 잘못된 Authorization 헤더가 있는 경우") {
            val request =
                MockHttpServletRequest().apply {
                    addHeader(AUTHORIZATION_HEADER, "Bearer $INVALID_TOKEN")
                }
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            every {
                jwtService.validate(INVALID_TOKEN)
            } returns false

            `when`("JwtFilter가 실행될 때") {
                jwtFilter.doFilter(request, response, filterChain)

                then("AuthenticationCreator는 호출되지 않아야 한다") {
                    verify(exactly = 0) { authenticationCreator.createByAccessToken(any()) }
                }

                then("SecurityContextHolder는 변경되지 않아야 한다") {
                    SecurityContextHolder.getContext().authentication shouldBe null
                }

                then("필터 체인이 계속 실행되어야 한다") {
                    verify { filterChain.doFilter(request, response) }
                }
            }
        }
    })
