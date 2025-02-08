package com.friends.message.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.chat.dto.PingPongDto
import com.friends.chat.dto.PingPongType
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.repository.PingPongRepository
import com.friends.common.util.JsonUtil
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.MessageNotFoundException
import com.friends.message.NotMessageSenderException
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.message.repository.MessageRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

@Service
class MessageCommandService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val virtualThreadExecutor: ExecutorService,
    private val pingPongRepository: PingPongRepository,
) {
    /**
     * 채팅방 ID를 키로 하고, 참여하고 있는 온라인 유저의 아이디를 값으로 하는 Map입니다.
     * ConcurrentHashMap을 사용하여 thread-safe하게 구현합니다.
     * value 의 MutableSet은 thread-safe 하지 않아서 ConcurrentHashMap.newKeySet()을 사용하여 thread-safe하게 구현합니다.
     */
    private val onlineUsers = ConcurrentHashMap<Long, MutableSet<Long>>()

    /**
     * 유저 ID를 키로 하고, 참여하고 있는 온라인 유저의 세션을 값으로 하는 Map입니다.
     * 하나의 유저가 여러개의 세션을 가질 수 있기 때문에 MutableSet을 사용합니다. (ex. 웹, 모바일 에서 동시 접속)
     */
    private val sessions = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()

    /**
     * ping message 를 보내어 pong message 를 받지 못할 경우 세션을 제거합니다.
     */
    @Scheduled(fixedRate = 30000) // 30초 마다 실행
    fun ping() {
        for (entry in sessions) {
            val userSessions = entry.value
            userSessions.forEach { session ->
                try {
                    if (pingPongRepository.existPing(session.id)) {
                        // pong 메세지를 받지 못할 경우 세션을 제거합니다.
                        session.close()
                        userSessions.remove(session)
                    }
                    session.sendMessage(TextMessage(JsonUtil.toJson(PingPongDto(PingPongType.PING.name.lowercase()))))
                    pingPongRepository.savePing(session.id)
                } catch (e: Exception) {
                    // 에러가 발생할 경우 세션을 제거합니다.
                    session.close()
                    userSessions.remove(session)
                }
            }
        }
    }

    /**
     * 테스트 유저를 만들어 채팅방을 참여시키면서 테스트 유저가 온라인 상태가 되는 문제가 생겼습니다.
     * 이를 해제하기 위해 사용할 clear 메서드 입니다.
     *
     * 테스트 환경에서만 사용할 목적의 메서드입니다.
     * 배포시에는 제거될 예정입니다.
     */
    fun clear() {
        onlineUsers.clear()
        sessions.clear()
        chatRoomLocks.clear()
    }

    /**
     * 채팅방 ID를 키로 하여, 해당 채팅방에서 메시지를 보낼 때 동기화에 사용할 Lock 객체를 관리합니다.
     */
    private val chatRoomLocks = ConcurrentHashMap<Long, Any>()

    /**
     * 참여하고 있는 모든 채팅방에 온라인 유저로 등록됩니다.
     */
    fun setAllChatRoomsOnline(
        memberId: Long,
        session: WebSocketSession,
    ) {
        sessions
            .computeIfAbsent(memberId) {
                ConcurrentHashMap.newKeySet()
            }.add(session)

        chatRoomMemberRepository
            .findAllByMemberId(memberId)
            .forEach { chatRoomMember ->
                onlineUsers
                    .computeIfAbsent(chatRoomMember.chatRoom.id) {
                        ConcurrentHashMap.newKeySet()
                    }.add(memberId)
            }
    }

    /**
     * 참여하고 있는 모든 채팅방에서 오프라인 상태가 됩니다.
     */
    fun setAllChatRoomsOffline(
        memberId: Long,
        session: WebSocketSession,
    ) {
        removeOnlineUserSession(memberId, session)

        chatRoomMemberRepository
            .findAllByMemberId(memberId)
            .forEach { chatRoomMember ->
                onlineUsers[chatRoomMember.chatRoom.id]?.remove(memberId)
            }
    }

    /**
     * 유저가 채팅방에 온라인 상태가 됩니다.
     */
    fun setChatRoomOnline(
        memberId: Long,
        chatRoomId: Long,
    ) {
        onlineUsers
            .computeIfAbsent(chatRoomId) {
                ConcurrentHashMap.newKeySet()
            }.add(memberId)
    }

    /**
     * 유저가 채팅방에 오프라인 상태가 됩니다.
     */
    fun setChatRoomOffline(
        memberId: Long,
        chatRoomId: Long,
    ) {
        onlineUsers[chatRoomId]?.remove(memberId)
    }

    private fun removeOnlineUserSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        sessions[memberId]?.removeIf {
            it.id == session.id
        }
    }

    /**
     * 채팅방을 나갈 때마다 마지막으로 읽은 메세지 ID를 업데이트합니다.
     * 채팅방이 없거나 멤버가 없거나 채팅방 멤버가 아닐 경우 무시합니다.
     */
    @Transactional
    fun disconnectChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: return // 채팅방이 없을 경우 무시
        val member = memberRepository.findById(memberId).orElse(null) ?: return // 멤버가 없을 경우 무시
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: return // 채팅방 멤버가 아닐 경우 무시

        // 마지막으로 읽은 메세지 ID 업데이트 (채팅방에 메세지가 없다면 무시)
        messageRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)?.let {
            chatRoomMember.lastReadMessage = it
        }
    }

    /**
     * 채팅방에 메세지를 보내고 메세지를 저장합니다.
     * 채팅방이 없거나 멤버가 없을 경우 예외를 발생시킵니다.
     */
    @Transactional
    fun sendMessage(
        chatRoomId: Long,
        memberId: Long,
        content: String,
        type: MessageType,
        clientMessageId: String? = null,
    ): Message {
        val chatRoomLock = chatRoomLocks.computeIfAbsent(chatRoomId) { Any() }

        // 채팅방 단위로 동기화 (synchronized block)
        synchronized(chatRoomLock) {
            /**
             * 메세지를 보낼 때마다 보낸 유저와 채팅방이 있는지 DB 에 확인합니다.
             * 이 과정이 비효율적일 경우 아래 프록시 객체를 생성하여 메세지를 보내는 로직을 고려합니다.
             * 프록시 객체는 DB 에서 채팅방과 유저 정보를 요청하지 않지만, DB 에 데이터가 있는지 없는지 확인할 수 없습니다.
             *
             * val chatRoom = entityManager.getReference(ChatRoom::class.java, chatRoomId)
             * val sender = entityManager.getReference(Member::class.java, message.senderId)
             */
            val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
            val sender = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
            val message = messageRepository.save(Message.of(chatRoom, sender, content, type))

            // 채팅방의 모든 온라인 유저에게 메세지 전송
            return sendAsynchronousMessage(message, clientMessageId)
        }
    }

    @Transactional
    fun sendMessage(
        message: Message,
    ): Message {
        val chatRoomId = message.chatRoom.id
        val chatRoomLock = chatRoomLocks.computeIfAbsent(chatRoomId) { Any() }

        synchronized(chatRoomLock) {
            return sendAsynchronousMessage(message)
        }
    }

    fun sendAsynchronousMessage(
        message: Message,
        clientMessageId: String? = null,
    ): Message {
        val chatRoomId = message.chatRoom.id
        val onlineUserIdSet = onlineUsers[chatRoomId] ?: return message
        val futures = mutableListOf<CompletableFuture<*>>()
        onlineUserIdSet.forEach { userId ->
            val userSessions = sessions[userId] ?: return@forEach
            userSessions.forEach { session ->
                val future =
                    CompletableFuture.supplyAsync({
                        if (session.isOpen) {
                            try {
                                val sendMessageDto =
                                    ChatSendMessageDto(
                                        clientMessageId,
                                        message.id,
                                        chatRoomId,
                                        message.sender.id,
                                        message.content,
                                        message.createdAt,
                                        message.type,
                                    )
                                session.sendMessage(TextMessage(JsonUtil.toJson(sendMessageDto)))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }, virtualThreadExecutor)
                futures.add(future)
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()
        return message
    }

    @Transactional
    fun deleteMessage(
        memberId: Long,
        messageId: Long,
    ) {
        val message = messageRepository.findById(messageId).orElseThrow { throw MessageNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        if (message.sender != member) throw NotMessageSenderException()
        message.content = "삭제된 메세지입니다."
        message.type = MessageType.DELETE_MESSAGE
        sendMessage(message)
    }
}
