package com.friends

import com.friends.config.AuthProperties
import com.friends.config.EmailProperties
import com.friends.config.JwtProperties
import com.friends.config.OAuth2Properties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class, AuthProperties::class, EmailProperties::class, OAuth2Properties::class)
@EnableScheduling
class FriendsServerApplication

fun main(args: Array<String>) {
    runApplication<FriendsServerApplication>(*args)
}
