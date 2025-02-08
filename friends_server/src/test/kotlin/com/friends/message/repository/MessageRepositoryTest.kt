package com.friends.message.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class MessageRepositoryTest(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : DescribeSpec(
        {
            lateinit var member: Member
            lateinit var member2: Member
            lateinit var chatRoom1: ChatRoom
            lateinit var chatRoom2: ChatRoom
            lateinit var chatRoomMember1: ChatRoomMember
            lateinit var chatRoomMember2: ChatRoomMember
            lateinit var message: Message
            beforeEach {
                member = memberRepository.save(createTestMember(email = "message@com2"))
                member2 = memberRepository.save(createTestMember(email = "message@com"))
                chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member))
                chatRoom2 = chatRoomRepository.save(createTestChatRoom(manager = member2))
                val enterMessage = messageRepository.save(createTestMessage(chatRoom1, member, Message.enterMessage(member.nickname), MessageType.SYSTEM_MEMBER_ENTER))
                messageRepository.save(createTestMessage(chatRoom1, member2, Message.enterMessage(member2.nickname), MessageType.SYSTEM_MEMBER_ENTER))
                message = messageRepository.save(createTestMessage(chatRoom1, member2))
                chatRoomMember1 = chatRoomMemberRepository.save(createTestChatRoomMember(member = member, chatRoom = chatRoom1, lastReadMessage = enterMessage))
                chatRoomMember2 = chatRoomMemberRepository.save(createTestChatRoomMember(member = member2, chatRoom = chatRoom1, lastReadMessage = message))
            }

            describe("countUnreadMessages 메서드는") {
                context("멤버의 채팅방 연관 정보가 주어졌을 떄 ") {
                    it("읽지 않은 메시지의 개수를 반환한다.") {
                        messageRepository.countUnreadMessages(chatRoomMember1) shouldBe 1 // 입장 메시지 제외하고 1개
                        messageRepository.countUnreadMessages(chatRoomMember2) shouldBe 0 // 모든 메시지를 읽었으므로 0개
                    }
                }
            }

            describe("findUnreadMessagesForMember 메서드는") {
                context("멤버의 채팅방 연관 정보가 주어졌을 떄 ") {
                    it("읽지 않은 메시지를 반환한다.") {
                        val unreadMessages = messageRepository.findUnreadMessagesForMember(chatRoomMember1)
                        for (unreadMessage in unreadMessages) {
                            println("${unreadMessage.id} ${unreadMessage.content}")
                        }

                        unreadMessages.size shouldBe 2 // 자신의 입장 메세지를 제외하고 2개
                        unreadMessages[1].id shouldBe message.id // 마지막 메세지는 message
                    }
                }
            }

            describe("findMessagesBeforeIdInChatRoom 메서드는 ") {
                context("채팅방의 메시지 id와 사이즈가 주어졌을 때") {
                    it("이전 메시지를 반환한다.") {
                        val previousMessages = messageRepository.findMessagesBeforeIdInChatRoom(chatRoom1, message.id, 5)
                        for (previousMessage in previousMessages) {
                            println("${previousMessage.id} ${previousMessage.content}")
                        }
                        previousMessages.content.size shouldBe 2 // 입장 메세지 2개
                        previousMessages.hasNext() shouldBe false // 이전 메시지가 없으므로 hasNext는 false
                    }
                }

                context("채팅방의 메시지 id가 주어졌을 때") {
                    it("최신 n 개 메시지를 반환한다.") {
                        val previousMessages = messageRepository.findMessagesBeforeIdInChatRoom(chatRoom = chatRoom1, size = 5)

                        previousMessages.content.size shouldBe 3 // 전체 메세지 3개
                        previousMessages.hasNext() shouldBe false // 이전 메시지가 없으므로 hasNext는 false
                    }
                }
                context("조회된 것 이전의 메세지가 존재한다면") {
                    it("hasNext는 true를 반환한다.") {
                        val previousMessages = messageRepository.findMessagesBeforeIdInChatRoom(chatRoom = chatRoom1, size = 2)
                        previousMessages.hasNext() shouldBe true
                    }
                }
            }
        },
    )
