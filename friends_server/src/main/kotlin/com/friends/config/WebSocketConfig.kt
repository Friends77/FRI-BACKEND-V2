package com.friends.config

import com.friends.alarm.websocket.AlarmWebsocketHandler
import com.friends.chat.websocket.AuthWebsocketInterceptor
import com.friends.chat.websocket.ChatWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatWebSocketHandler: ChatWebSocketHandler,
    private val authWebsocketInterceptor: AuthWebsocketInterceptor,
    private val alarmWebsocketHandler: AlarmWebsocketHandler,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(chatWebSocketHandler, "/ws/chat")
            .addInterceptors(authWebsocketInterceptor)
            .setAllowedOrigins("*") // CORS 허용

        registry
            .addHandler(alarmWebsocketHandler, "/ws/alarm")
            .addInterceptors(authWebsocketInterceptor)
            .setAllowedOrigins("*") // CORS 허용
    }
}
