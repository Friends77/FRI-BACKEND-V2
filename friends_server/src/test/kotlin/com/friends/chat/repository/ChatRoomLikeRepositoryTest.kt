package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomLike
import com.friends.member.MEMBER_OTHER_EMAIL
import com.friends.member.MEMBER_OTHER_NICKNAME
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ChatRoomLikeRepositoryTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomLikeRepository: ChatRoomLikeRepository,
) : DescribeSpec({
        lateinit var member1: Member
        lateinit var member2: Member
        lateinit var chatRoom1: ChatRoom
        beforeEach {
            member1 = memberRepository.save(createTestMember())
            member2 = memberRepository.save(createTestMember(email = MEMBER_OTHER_EMAIL, nickname = MEMBER_OTHER_NICKNAME))
            chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member1))
            chatRoomLikeRepository.save(ChatRoomLike.of(chatRoom1, member1))
        }

        describe("existsByChatRoomAndMemberId 메서드는") {
            context("채팅방과 회원을 받으면") {
                it("채팅방 좋아요 여부를 반환한다") {
                    chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom1, member1.id) shouldBe true
                    chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom1, member2.id) shouldBe false
                }
            }
        }

        describe("deleteByChatRoomAndMember 메서드는") {
            context("채팅방과 회원을 받으면") {
                it("채팅방 좋아요를 삭제한다") {
                    chatRoomLikeRepository.deleteByChatRoomAndMember(chatRoom1, member1)
                    chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom1, member1.id) shouldBe false
                }
            }
        }

        describe("countByChatRoom 메서드는") {
            context("채팅방을 받으면") {
                it("채팅방 좋아요 수를 반환한다") {
                    chatRoomLikeRepository.countByChatRoom(chatRoom1) shouldBe 1
                }
            }
        }
    })
