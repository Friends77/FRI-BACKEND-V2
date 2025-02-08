package com.friends.support.config

import com.friends.member.MEMBER_ID
import com.friends.member.entity.Role
import com.friends.security.securityException.JwtFilterAccessDeniedHandler
import com.friends.security.securityException.JwtFilterAuthenticationEntryPoint
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@TestConfiguration
class TestSpringSecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }

        http
            .addFilterBefore(MockPassFilter(), UsernamePasswordAuthenticationFilter::class.java) //MockPassFilter로 인증 처리
            .exceptionHandling {
                it
                    .accessDeniedHandler(JwtFilterAccessDeniedHandler())
                    .authenticationEntryPoint(JwtFilterAuthenticationEntryPoint())
            }

        http.authorizeHttpRequests {
            it
                .requestMatchers("/api/auth/**", "/api/global/**")
                .permitAll()
                .requestMatchers("/api/user/**")
                .hasAuthority(Role.ROLE_USER.name)
                .requestMatchers("/api/admin/**")
                .hasAuthority(Role.ROLE_ADMIN.name)
                .anyRequest()
                .authenticated()
        }
        return http.build()
    }

    class MockPassFilter : OncePerRequestFilter() {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain,
        ) {
            if (request.headerNames.toList().contains("user_id")) {
                val memberId = MEMBER_ID
                val authorities = listOf(Role.ROLE_USER, Role.ROLE_ADMIN).map { SimpleGrantedAuthority(it.name) }
                val authentication: Authentication = UsernamePasswordAuthenticationToken(memberId, null, authorities)
                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)
        }
    }
}
