plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "friends"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // DB
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.redisson:redisson-spring-boot-starter:3.30.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.2")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.2")
    implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.5.2")
    implementation("org.hibernate.orm:hibernate-spatial:6.5.2.Final")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // web client
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // email
    implementation("org.springframework.boot:spring-boot-starter-mail")
    // thyemleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // configuration property
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // aws
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.375")

    // websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Test
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.0")
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// https://github.com/Ninja-Squad/springmockk/blob/master/README.md#gotchas
tasks.test {
    jvmArgs("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
}
