package com.friends.common.util

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object JsonUtil {
    val objectMapper =
        jacksonObjectMapper()
            .registerModules(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISO-8601 형식 활성화 -> "2024-12-19T12:31:49.478613" 형식으로 출력

    /**
     * JSON 문자열을 객체로 변환
     * inline 으로 선언된 함수는 호출 시 함수의 본문이 호출문으로 대체되는 함수입니다.
     * reified 키워드는 함수의 타입 매개변수를 런타임에 알 수 있게 해줍니다.
     */
    inline fun <reified T> fromJson(json: String): T = objectMapper.readValue(json)

    // 객체를 JSON 문자열로 변환
    fun toJson(obj: Any): String = objectMapper.writeValueAsString(obj)
}
