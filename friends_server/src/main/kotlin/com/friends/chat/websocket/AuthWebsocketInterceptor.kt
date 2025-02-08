package com.friends.chat.websocket

import com.friends.secondaryToken.SecondaryTokenService
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

@Component
class AuthWebsocketInterceptor(
    private val secondaryTokenService: SecondaryTokenService,
) : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        /**
         * WebSocket 연결 요청 시 http 방식으로 handshake 하는 스레드에서 실행됩니다.
         * 연결 이후에 사용자 인증 정보를 사용할 수 있도록 WebSocketHandler에서 attributes에 저장합니다.
         */
        val queryParams = (request as ServletServerHttpRequest).servletRequest.parameterMap
        val token = queryParams["token"]?.firstOrNull() ?: throw IllegalArgumentException("Token is required")
        val memberId = secondaryTokenService.getMemberId(token)
        attributes["MEMBER_ID"] = memberId
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {
        // 아무 작업도 필요 없음
    }
}
