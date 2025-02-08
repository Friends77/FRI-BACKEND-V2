package com.friends.chat.websocket

import com.friends.chat.UnexpectedChatRoomException
import com.friends.chat.dto.ChatErrorMessageDto
import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.chat.dto.PingPongDto
import com.friends.chat.dto.PingPongType
import com.friends.chat.repository.PingPongRepository
import com.friends.common.util.JsonUtil
import com.friends.message.service.MessageCommandService
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler

private val logger = KotlinLogging.logger {}

@Component
class ChatWebSocketHandler(
    private val messageCommandService: MessageCommandService,
    private val pingPongRepository: PingPongRepository,
) : TextWebSocketHandler() {
    companion object {
        private const val SEND_TIME_LIMIT = 2000 // 2초
        private const val BUFFER_SIZE_LIMIT = 1024 * 1024 // 1MB
        private const val TEXT_MAX_SIZE = 1 * 1024 * 1024 // 1MB
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        try {
            val memberId = getMemberId(session)
            session.textMessageSizeLimit = TEXT_MAX_SIZE
            messageCommandService.setAllChatRoomsOnline(memberId, ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT))
        } catch (e: Exception) {
            session.close(CloseStatus.SERVER_ERROR)// 채팅방 연결 종료 후 에러 처리
            throw UnexpectedChatRoomException(e)
        }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        // ping / pong
        try {
            val pingPongDto = JsonUtil.fromJson<PingPongDto>(message.payload)
            if (pingPongDto.type.equals(PingPongType.PONG.name, ignoreCase = true)) {
                pingPongRepository.deletePing(session.id)
                return
            }
        } catch (e: Exception) {
            // ignore
        }

        val chatMessage =
            try {
                JsonUtil.fromJson<ChatReceiveMessageDto>(message.payload)
            } catch (e: Exception) {
                logger.error(e) { "메세지 전송 양식이 부적절합니다. " + message.payload }
                val chatErrorMessageDto =
                    ChatErrorMessageDto(
                        clientMessageId = null,
                        code = 400,
                        message = "메세지 전송 양식이 부적절합니다.",
                    )
                session.sendMessage(TextMessage(JsonUtil.toJson(chatErrorMessageDto)))
                return
            }

        try {
            val memberId = getMemberId(session)

            messageCommandService.sendMessage(
                chatMessage.chatRoomId,
                memberId,
                chatMessage.content,
                chatMessage.type,
                chatMessage.clientMessageId,
            )
        } catch (e: Exception) {
            val chatErrorMessageDto =
                ChatErrorMessageDto(
                    clientMessageId = chatMessage.clientMessageId,
                    code = 500,
                    message = e.message ?: "알 수 없는 오류가 발생했습니다.",
                )
            session.sendMessage(TextMessage(JsonUtil.toJson(chatErrorMessageDto)))
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        /**
         * 웹소켓 연결이 종료되면 온라인 유저 목록에서 제거됩니다.
         */
        val memberId = getMemberId(session)
        messageCommandService.setAllChatRoomsOffline(memberId, session)
        // TODO : 채팅방 나가기 실패 시 에러 처리
    }

    private fun getMemberId(session: WebSocketSession): Long = session.attributes["MEMBER_ID"] as Long
}
