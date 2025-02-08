package com.friends.config

import com.friends.jwt.JwtService
import com.friends.member.entity.Role
import com.friends.security.authentication.AuthenticationCreator
import com.friends.security.filter.JwtFilter
import com.friends.security.securityException.JwtFilterAccessDeniedHandler
import com.friends.security.securityException.JwtFilterAuthenticationEntryPoint
import com.friends.security.userDetails.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtFilterAccessDeniedHandler: JwtFilterAccessDeniedHandler,
    private val jwtFilterAuthenticationEntryPoint: JwtFilterAuthenticationEntryPoint,
    private val authenticationCreator: AuthenticationCreator,
    private val jwtService: JwtService,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }

        http
            .addFilterBefore(JwtFilter(authenticationCreator, jwtService), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it
                    .accessDeniedHandler(jwtFilterAccessDeniedHandler)
                    .authenticationEntryPoint(jwtFilterAuthenticationEntryPoint)
            }

        http.authorizeHttpRequests {
            it
                .requestMatchers(
                    "/api/auth/**",
                    "/api/global/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "ws/chat/**",
                    "ws/alarm/**",
                ).permitAll()
                .requestMatchers("/api/user/**")
                .hasAuthority(Role.ROLE_USER.name)
                .requestMatchers("/api/admin/**")
                .hasAuthority(Role.ROLE_ADMIN.name)
                .anyRequest()
                .authenticated()
        }

        return http.build()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider =
        DaoAuthenticationProvider().apply {
            setUserDetailsService(customUserDetailsService)
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager = authConfig.authenticationManager
}
