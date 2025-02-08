package com.friends.chat.service

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.AgeRangeEnum
import com.friends.chat.entity.GenderRatioEnum
import com.friends.chat.repository.ChatRoomMemberAgeRangeRepository
import com.friends.chat.repository.ChatRoomMemberGenderRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.createTestMember
import com.friends.message.createTestMessage
import com.friends.profile.createTestProfile
import com.friends.profile.entity.GenderEnum
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class ChatRoomRecommendationCriteriaServiceTest :
    BehaviorSpec(
        {
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val chatRoomMemberGenderRepository = mockk<ChatRoomMemberGenderRepository>()
            val chatRoomMemberAgeRangeRepository = mockk<ChatRoomMemberAgeRangeRepository>()
            val chatRoomRecommendationCriteriaService = ChatRoomRecommendationCriteriaService(chatRoomRepository, chatRoomMemberRepository, chatRoomMemberAgeRangeRepository, chatRoomMemberGenderRepository)

            given("extractMemberMostAgeRange") {
                val chatRoom = createTestChatRoom()
                every { chatRoomRepository.findAll() } returns listOf(chatRoom)
                every { chatRoomMemberAgeRangeRepository.save(any(), any()) } returns Unit
                `when`("2개 이상 나이대가 가장 많은 경우") {
                    every { chatRoomMemberRepository.findByChatRoom(any()) } returns
                        listOf(
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test1", email = "test1", profile = createTestProfile(birth = LocalDate.now().minusYears(30).minusDays(1)))), // 30세
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test2", email = "test2", profile = createTestProfile(birth = LocalDate.now().minusYears(30).plusDays(1)))), // 29세
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test3", email = "test3", profile = createTestProfile(birth = LocalDate.now().minusYears(40).minusDays(1))), lastReadMessage = createTestMessage()), // 40세
                        )
                    then("가장 많은 나이대를 모두 저장한다.") {
                        chatRoomRecommendationCriteriaService.extractMemberMostAgeRange()
                        verify(exactly = 1) {
                            chatRoomMemberAgeRangeRepository.save(chatRoom.id, AgeRangeEnum.TWENTY.value.toString())
                        }
                    }
                }

                `when`("특정 나이대가 가장 많은 경우") {
                    every { chatRoomMemberRepository.findByChatRoom(any()) } returns
                        listOf(
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test1", email = "test1", profile = createTestProfile(birth = LocalDate.now().minusYears(60).minusDays(1))), lastReadMessage = createTestMessage()), //60세
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test2", email = "test2", profile = createTestProfile(birth = LocalDate.now().minusYears(80).plusDays(1))), lastReadMessage = createTestMessage()), // 79세
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test3", email = "test3", profile = createTestProfile(birth = LocalDate.now().minusYears(25).minusDays(1))), lastReadMessage = createTestMessage()), // 24세
                        )
                    then("가장 많은 나이대를 저장한다.") {
                        chatRoomRecommendationCriteriaService.extractMemberMostAgeRange()
                        verify(exactly = 1) {
                            chatRoomMemberAgeRangeRepository.save(chatRoom.id, AgeRangeEnum.ABOVE_SIXTY.value.toString())
                        }
                    }
                }

                `when`("아무런 데이터가 없을 때") {
                    every { chatRoomMemberRepository.findByChatRoom(any()) } returns
                        listOf(
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember()),
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember()),
                        )
                    then("저장하지 않는다..") {
                        chatRoomRecommendationCriteriaService.extractMemberMostAgeRange()
                        verify(exactly = 0) {
                            chatRoomMemberAgeRangeRepository.save(any(), any())
                        }
                    }
                }
            }

            given("extractMemberMostGender") {
                val chatRoom = createTestChatRoom()
                every { chatRoomRepository.findAll() } returns listOf(chatRoom)
                every { chatRoomMemberGenderRepository.save(any(), any()) } returns Unit
                `when`("남자와 여자 수가 같을 때") {
                    every { chatRoomMemberRepository.findByChatRoom(any()) } returns
                        listOf(
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test1", email = "test1", profile = createTestProfile(gender = GenderEnum.MAN))),
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test2", email = "test2", profile = createTestProfile(gender = GenderEnum.WOMAN))),
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test3", email = "test3", profile = createTestProfile(gender = GenderEnum.MAN))),
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test4", email = "test4", profile = createTestProfile(gender = GenderEnum.WOMAN))),
                        )

                    then("남자와 여자를 저장한다.") {
                        chatRoomRecommendationCriteriaService.extractMemberMostGender()
                        verify(exactly = 1) {
                            chatRoomMemberGenderRepository.save(chatRoom.id, GenderRatioEnum.EQUAL.toString())
                        }
                    }
                }

                `when`("남자가 더 많을 때") {
                    every { chatRoomMemberRepository.findByChatRoom(any()) } returns
                        listOf(
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test1", email = "test1", profile = createTestProfile(gender = GenderEnum.MAN))),
                        )
                    then("남자를 저장한다.") {
                        chatRoomRecommendationCriteriaService.extractMemberMostGender()
                        verify(exactly = 1) {
                            chatRoomMemberGenderRepository.save(chatRoom.id, GenderRatioEnum.MAJORITY_MAN.toString())
                        }
                    }
                }

                `when`("아무런 데이터가 없을 때") {
                    every { chatRoomMemberRepository.findByChatRoom(any()) } returns
                        listOf(
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test1", email = "test1", profile = createTestProfile(gender = GenderEnum.ETC))),
                            createTestChatRoomMember(chatRoom = chatRoom, member = createTestMember(nickname = "test2", email = "test2", profile = createTestProfile(gender = GenderEnum.ETC))),
                        )
                    then("저장하지 않는다.") {
                        chatRoomRecommendationCriteriaService.extractMemberMostGender()
                        verify(exactly = 0) {
                            chatRoomMemberGenderRepository.save(any(), any())
                        }
                    }
                }
            }
        },
    )
