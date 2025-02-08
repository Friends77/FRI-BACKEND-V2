package com.friends.message

import com.friends.chat.createTestChatRoom
import com.friends.chat.entity.ChatRoom
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime

const val TEST_CONTENT = "안녕하세여"

fun createTestMessage(
    chatRoom: ChatRoom = createTestChatRoom(),
    sender: Member = createTestMember(),
    content: String = TEST_CONTENT,
    type: MessageType = MessageType.TEXT,
) = Message.of(content = content, chatRoom = chatRoom, sender = sender, type = type)

fun createMockTestMessage(
    id: Long = 0L,
    chatRoom: ChatRoom = createTestChatRoom(),
    sender: Member = createTestMember(),
    content: String = TEST_CONTENT,
    type: MessageType = MessageType.TEXT,
) = Message(id, chatRoom, sender, content, type).apply { ReflectionTestUtils.setField(this, "createdAt", LocalDateTime.now()) } //ReflectionTestUtils을 사용하면 private field에 값을 넣을 수 있다.
