package com.friends.secondaryToken

import com.friends.security.securityException.InvalidTokenException
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.Base64

@Service
class SecondaryTokenService(
    private val secondaryTokenRepository: SecondaryTokenRepository,
) {
    /**
     * memberId를 통해 code를 생성하고 저장합니다.
     * code 의 유효시간은 5분입니다.
     */
    fun createAndSaveSecondaryToken(
        memberId: Long,
    ): String {
        val code = createRandomToken(32)
        secondaryTokenRepository.save(code, memberId, 300)
        return code
    }

    /**
     * code를 통해 memberId를 가져옵니다.
     * code는 한 번만 사용할 수 있습니다.
     */
    fun getMemberId(code: String): Long {
        secondaryTokenRepository.getMemberId(code)?.let {
            secondaryTokenRepository.delete(code)
            return it
        } ?: throw InvalidTokenException()
    }

    private fun createRandomToken(length: Int): String {
        val random = SecureRandom() // 안전한 난수를 생성합니다.
        val bytes = ByteArray(length) // 길이만큼의 바이트 배열을 생성합니다.
        random.nextBytes(bytes) // 바이트 배열에 난수를 채웁니다.
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes) // 바이트 배열을 Base64로 url-safe하게 인코딩합니다.
    }
}
