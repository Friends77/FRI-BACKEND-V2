package com.friends.chat.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.PositiveLikeCountException
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomLike
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ChatRoomLikeCommandServiceTest :
    BehaviorSpec(
        {
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val memberRepository = mockk<MemberRepository>()
            val chatRoomLikeRepository = mockk<ChatRoomLikeRepository>()
            val chatRoomLikeCommandService = ChatRoomLikeCommandService(chatRoomRepository, chatRoomLikeRepository, memberRepository)

            given("toggleLike 테스트") {
                var chatRoom = createTestChatRoom(likeCount = 1)
                val member = createTestMember()
                every { chatRoomRepository.findById(any()) } returns Optional.of(chatRoom)
                every { memberRepository.findById(any()) } returns Optional.of(member)
                every { chatRoomLikeRepository.existsByChatRoomAndMemberId(any(), any()) } returns false
                every { chatRoomLikeRepository.save(any()) } returns createTestChatRoomLike()
                `when`("좋아요를 누르지 않은 상태에서 좋아요를 누를 경우") {
                    then("좋아요가 저장된다.") {
                        chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id)
                        chatRoom.likeCount shouldBe 2
                        verify(exactly = 1) {
                            chatRoomLikeRepository.save(any())
                            chatRoom.increaseLikeCount()
                        }
                    }
                }

                every { chatRoomLikeRepository.existsByChatRoomAndMemberId(any(), any()) } returns true
                every { chatRoomLikeRepository.deleteByChatRoomAndMember(any(), any()) } returns Unit
                `when`("좋아요를 누른 상태에서 좋아요를 누를 경우") {
                    then("좋아요가 삭제된다.") {
                        chatRoom.likeCount shouldBe 1
                        chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id)
                        chatRoom.likeCount shouldBe 0
                        verify(exactly = 0) {
                            chatRoomLikeRepository.save(any())
                        }
                    }
                }

                `when`("존재하지 않는 채팅방 ID를 전달할 경우") {
                    then("ChatRoomNotFoundException 발생한다.") {
                        every { chatRoomRepository.findById(any()) } returns Optional.empty()
                        shouldThrow<ChatRoomNotFoundException> { chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id) }
                    }
                }

                `when`("좋아요 감소 시 좋아요 수가 음수가 되는 경우") {
                    chatRoom = createTestChatRoom(likeCount = 0)
                    every { chatRoomRepository.findById(any()) } returns Optional.of(chatRoom)
                    every { chatRoomLikeRepository.existsByChatRoomAndMemberId(any(), any()) } returns true
                    every { chatRoomLikeRepository.deleteByChatRoomAndMember(any(), any()) } returns Unit
                    then("PositiveLikeCountException 발생한다.") {
                        shouldThrow<PositiveLikeCountException> { chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id) }
                    }
                }
            }
        },
    )
