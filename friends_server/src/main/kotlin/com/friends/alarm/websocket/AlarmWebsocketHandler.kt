package com.friends.alarm.websocket

import com.friends.alarm.service.AlarmCommandService
import com.friends.chat.UnexpectedChatRoomException
import com.friends.chat.repository.PingPongRepository
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class AlarmWebsocketHandler(
    private val alarmCommandService: AlarmCommandService,
    private val pingPongRepository: PingPongRepository,
) : TextWebSocketHandler() {
    companion object {
        private const val SEND_TIME_LIMIT = 2000 // 2초
        private const val BUFFER_SIZE_LIMIT = 1024 * 1024 // 1MB
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        try {
            alarmCommandService.addOnlineUserSession(
                memberId = getMemberId(session),
                session = ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT),
            )
        } catch (e: Exception) {
            session.close(CloseStatus.SERVER_ERROR)
            throw UnexpectedChatRoomException(e)
        }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        // ping / pong
        if (message.payload.equals("pong", ignoreCase = true)) {
            pingPongRepository.deletePing(session.id)
            return
        }

        // 알람 전송의 경우 REST API 에서 웹소켓 세션을 활용합니다.
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        alarmCommandService.removeOnlineUserSession(getMemberId(session), session)
    }

    private fun getMemberId(session: WebSocketSession): Long = session.attributes["MEMBER_ID"] as Long
}
