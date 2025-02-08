package com.friends.chat.service

import com.friends.chat.CHAT_ROOM_BASE_IMAGE_URL
import com.friends.chat.ChatRoomMemberNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.TEST_CHAT_ROOM_ID
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomList
import com.friends.chat.dto.mapper.ChatRoomResponseMapper
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.createTestMemberWithId
import com.friends.member.repository.MemberRepository
import com.friends.message.createMockTestMessage
import com.friends.message.repository.MessageRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ChatRoomQueryServiceTest :
    BehaviorSpec(
        {
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val messageRepository = mockk<MessageRepository>()
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val chatRoomLikeRepository = mockk<ChatRoomLikeRepository>()
            val memberRepository = mockk<MemberRepository>()
            val friendshipRepository = mockk<FriendShipRepository>()
            val chatRoomBaseImageUrl = CHAT_ROOM_BASE_IMAGE_URL
            val profileBaseImageUrl = CHAT_ROOM_BASE_IMAGE_URL
            val chatRoomResponseMapper = ChatRoomResponseMapper(profileBaseImageUrl)
            val chatRoomQueryService =
                ChatRoomQueryService(
                    chatRoomMemberRepository,
                    messageRepository,
                    chatRoomRepository,
                    chatRoomLikeRepository,
                    memberRepository,
                    friendshipRepository,
                    chatRoomBaseImageUrl,
                    profileBaseImageUrl,
                    chatRoomResponseMapper,
                )

            given("getChatRooms 메소드 테스트") {
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                `when`("정상적인 조회 정보가 들어올 경우") {
                    every { chatRoomMemberRepository.countByChatRoom(any()) } returns 10
                    every { chatRoomMemberRepository.findAllByMemberAndFriends(any(), any()) } returns createTestChatRoomList()
                    every { messageRepository.countUnreadMessages(any()) } returns 0
                    every { chatRoomMemberRepository.findRepresentativeProfileByChatRoomId(any()) } returns listOf(createTestMember())
                    every { messageRepository.findRecentMessageInChatRoom(any()) } returns createMockTestMessage()
                    then("채팅방이 조회된다.") {
                        chatRoomQueryService.getChatRooms(MEMBER_ID, null)
                    }
                }

                `when`("친구 중 해당 닉네임을 가진 친구가 없는 경우") {
                    every { friendshipRepository.findFriendshipByMemberIdAndNickname(any(), any()) } returns emptyList()
                    then("빈 리스트가 반환된다.") {
                        chatRoomQueryService.getChatRooms(MEMBER_ID, "test")
                        verify(exactly = 0) {
                            chatRoomMemberRepository.findAllByMemberAndFriends(any(), any())
                        }
                    }
                }

                `when`("해당 회원이 참여하고 있는 채팅방이 없는 경우") {
                    every { chatRoomMemberRepository.findAllByMemberAndFriends(any(), any()) } returns emptyList()
                    then("빈 리스트가 반환된다.") {
                        chatRoomQueryService.getChatRooms(MEMBER_ID, null)
                        verify(exactly = 0) {
                            chatRoomMemberRepository.countByChatRoom(any())
                            messageRepository.countUnreadMessages(any())
                        }
                    }
                }
            }

            given("getChatRoomDetail 메소드 테스트") {
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                `when`("정상적인 조회 정보가 들어올 경우") {
                    every { chatRoomMemberRepository.countByChatRoom(any()) } returns 10
                    every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom())
                    every { chatRoomLikeRepository.existsByChatRoomAndMemberId(any(), any()) } returns true
                    every { chatRoomMemberRepository.findByChatRoomAndMember(any(), any()) } returns null
                    every { messageRepository.findRecentMessageInChatRoom(any()) } returns null
                    every { messageRepository.findRecentMessageInChatRoomWithSystemMessage(any()) } returns null
                    then("채팅방이 조회된다.") {
                        chatRoomQueryService.getChatRoomDetail(TEST_CHAT_ROOM_ID, MEMBER_ID)
                    }
                }

                `when`("존재하지 않는 채팅방인 경우") {
                    every { chatRoomRepository.findById(any()) } returns Optional.empty()
                    then("ChatRoomNotFoundException 에러가 발생한다.") {
                        shouldThrow<ChatRoomNotFoundException> { chatRoomQueryService.getChatRoomDetail(TEST_CHAT_ROOM_ID, MEMBER_ID) }
                    }
                }
            }

            given("getChatRoomMemberInfoList 메소드 테스트") {
                val requestMember = createTestMemberWithId(id = 1L)
                val manager = createTestMemberWithId(id = 2L)
                val members = listOf(createTestMemberWithId(id = 3L), createTestMemberWithId(id = 4L))
                every { memberRepository.findById(requestMember.id) } returns Optional.of(requestMember)
                every { friendshipRepository.findByRequesterAndReceiver(any(), any()) } returns null
                every { chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(any(), any()) } returns true
                `when`("요청자와 매니저가 다른 경우") {
                    every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom(manager = manager))
                    every { memberRepository.findById(manager.id) } returns Optional.of(manager)
                    every { chatRoomMemberRepository.findMemberByChatRoomAndMemberExceptManager(any(), any(), any()) } returns members
                    then("채팅방 멤버 정보가 조회된다.") {
                        chatRoomQueryService.getChatRoomMemberInfoList(TEST_CHAT_ROOM_ID, requestMember.id).map { it.id } shouldBe listOf(requestMember.id, manager.id, members[0].id, members[1].id)
                    }
                }

                `when`("요청자와 매니저가 같은 경우") {
                    every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom(manager = requestMember))
                    every { chatRoomMemberRepository.findMemberByChatRoomAndMemberExceptManager(any(), any(), any()) } returns emptyList()
                    then("채팅방 멤버 정보가 조회된다.") {
                        chatRoomQueryService.getChatRoomMemberInfoList(TEST_CHAT_ROOM_ID, requestMember.id).map { it.id } shouldBe listOf(requestMember.id)
                    }
                }
            }

            given("getChatRoomMemberInfo 메소드 테스트") {
                val requestMember = createTestMemberWithId(id = 1L)
                val newMember = createTestMemberWithId(id = 2L)
                every { memberRepository.findById(requestMember.id) } returns Optional.of(requestMember)
                every { memberRepository.findById(newMember.id) } returns Optional.of(newMember)
                every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom(manager = newMember))
                every { friendshipRepository.findByRequesterAndReceiver(any(), any()) } returns null
                `when`("정상적인 요청이 들어올 경우") {
                    every { chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(any(), any()) } returns true
                    then("채팅방 멤버 정보가 조회된다.") {
                        chatRoomQueryService.getChatRoomMemberInfo(TEST_CHAT_ROOM_ID, requestMember.id, newMember.id).id shouldBe newMember.id
                    }
                }

                `when`("요청자나 찾고자하는 멤버가 채팅방에 없는 경우") {
                    every { chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(any(), any()) } returns false
                    then("ChatRoomMemberNotFoundException 에러가 발생한다.") {
                        shouldThrow<ChatRoomMemberNotFoundException> { chatRoomQueryService.getChatRoomMemberInfo(TEST_CHAT_ROOM_ID, requestMember.id, newMember.id) }
                    }
                }
            }
        },
    )
