package com.friends.support.annotation

import com.friends.security.securityException.JwtFilterAuthenticationEntryPoint
import com.friends.support.config.TestSpringSecurityConfig
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(TestSpringSecurityConfig::class, JwtFilterAuthenticationEntryPoint::class)
annotation class ControllerTest
