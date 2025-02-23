package com.friends.common.converter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import java.lang.reflect.Type

// "Content-Type: multipart/form-data" 헤더를 지원하는 HTTP 요청 변환기
@Component
class MultipartJackson2HttpMessageConverter(
    objectMapper: ObjectMapper,
) : AbstractJackson2HttpMessageConverter(objectMapper, MediaType.APPLICATION_OCTET_STREAM) {
    override fun canWrite(
        clazz: Class<*>,
        mediaType: MediaType?,
    ): Boolean = false

    override fun canWrite(
        type: Type?,
        clazz: Class<*>,
        mediaType: MediaType?,
    ): Boolean = false

    override fun canWrite(mediaType: MediaType?): Boolean = false
}
