package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.member.MEMBER_OTHER_EMAIL
import com.friends.member.MEMBER_OTHER_NICKNAME
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import com.friends.message.repository.MessageRepository
import com.friends.profile.createTestProfile
import com.friends.profile.entity.Profile
import com.friends.profile.repository.ProfileRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ChatRoomMemberRepositoryTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageRepository: MessageRepository,
    private var profileRepository: ProfileRepository,
) : DescribeSpec({

        lateinit var profile: Profile
        lateinit var member1: Member
        lateinit var member2: Member
        lateinit var member3: Member
        lateinit var chatRoom1: ChatRoom
        lateinit var chatRoom2: ChatRoom
        lateinit var chatRoom3: ChatRoom
        lateinit var chatRoomMember: ChatRoomMember
        lateinit var chatRoomMember2: ChatRoomMember
        lateinit var chatRoomMember3: ChatRoomMember
        lateinit var chatRoomMember4: ChatRoomMember
        lateinit var chatRoomMember5: ChatRoomMember
        lateinit var message: Message

        beforeEach {
            member1 = memberRepository.save(createTestMember())
            member2 = memberRepository.save(createTestMember(email = MEMBER_OTHER_EMAIL, nickname = MEMBER_OTHER_NICKNAME))
            profile = profileRepository.save(createTestProfile(member2))
            member2.profile = profile
            member3 = memberRepository.save(createTestMember(email = MEMBER_OTHER_EMAIL + 2, nickname = MEMBER_OTHER_NICKNAME + 2))
            chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member1))
            chatRoom2 = chatRoomRepository.save(createTestChatRoom(manager = member2))
            chatRoom3 = chatRoomRepository.save(createTestChatRoom(manager = member3))
            message = messageRepository.save(createTestMessage(chatRoom1, member1))
            chatRoomMember = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom1, member1, message))
            chatRoomMember2 = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom1, member2, message))
            chatRoomMember3 = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom1, member3, message))
            chatRoomMember4 = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom2, member3, message))
            chatRoomMember5 = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom3, member2, message))
        }

        describe("countByChatRoomId 메서드는") {
            context("존재하는 채팅방 ID를 받으면") {
                it("chatRoomMember의 수를 반환한다") {
                    chatRoomMemberRepository.countByChatRoom(chatRoom1) shouldBe 3
                }
            }
        }

        describe("findAllByMemberAndFriends 메서드는") {
            context("memberId만 받으면") {
                it("chatRoomMember를 전부 반환한다") {
                    chatRoomMemberRepository.findAllByMemberAndFriends(member2.id, null) shouldBe listOf(chatRoomMember5, chatRoomMember2)
                }
            }

            context("회원 ID 리스트를 받으면") {
                it("chatRoomMember를 전부 반환한다") {
                    chatRoomMemberRepository.findAllByMemberAndFriends(member2.id, listOf(member1, member3)) shouldBe listOf(chatRoomMember2)
                }
            }
        }

        describe("existsByMemberIdAndChatRoomId") {
            context("존재하는 ChatRoomId와 MemberId가 들어오는 경우") {
                it("true를 반환한다.") {
                    chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom1, member1) shouldBe true
                }
            }

            context("존재하지 않는 ChatRoomId와 MemberId가 들어오는 경우") {
                it("false를 반환한다.") {
                    chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom3, member1) shouldBe false
                }
            }
        }

        describe("findByChatRoomAndMember") {
            context("참여중인 채팅방을 조회하면") {
                it("해당 채팅방을 반환한다") {
                    chatRoomMemberRepository.findByChatRoomAndMember(chatRoom1, member1)?.id shouldBe chatRoomMember.id
                }
            }
        }

        describe("findFirstByChatRoomOrderByCreatedAt") {
            context("채팅방을 조회하면") {
                it("먼저 들어온 채팅방멤버 연관 엔티티를 반환한다") {
                    chatRoomMemberRepository.findFirstByChatRoomOrderByCreatedAt(chatRoom1).id shouldBe chatRoomMember.id
                }
            }
        }

        describe("findRepresentativeProfileByChatRoomId") {
            context("채팅방 ID를 받으면") {
                it("프로필 이미지를 반환한다") {
                    chatRoomMemberRepository.findRepresentativeProfileByChatRoomId(chatRoom1.id).map { member -> member.profile?.imageUrl ?: "profileBaseImageUrl" } shouldBe listOf("profileBaseImageUrl", profile.imageUrl, "profileBaseImageUrl")
                }
            }
        }

        describe("findMemberByChatRoomAndMemberExceptManager") {
            context("채팅방과 회원을 받으면") {
                it("해당 채팅방에 참여중인 회원을 반환한다") {
                    chatRoomMemberRepository.findMemberByChatRoomAndMemberExceptManager(chatRoom1, member1, null).map { member -> member.id } shouldBe listOf(member2.id, member3.id)
                }
            }

            context("채팅방과 회원, 매니저를 받으면") {
                it("해당 채팅방에 참여중인 회원을 반환한다") {
                    chatRoomMemberRepository.findMemberByChatRoomAndMemberExceptManager(chatRoom1, member2, member1).map { member -> member.id } shouldBe listOf(member3.id)
                }
            }

            context("요청자와 매니저만 채팅방에 참여중인 경우") {
                it("emptyList를 반환한다") {
                    chatRoomMemberRepository.findMemberByChatRoomAndMemberExceptManager(chatRoom2, member3, member2).map { member -> member.id } shouldBe emptyList()
                }
            }
        }
    })
