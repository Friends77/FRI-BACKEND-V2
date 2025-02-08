package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.entity.ChatRoom
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec

@RepositoryTest
class ChatRoomRepositoryKtTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
) : DescribeSpec(
        {
            lateinit var member: Member
            lateinit var chatRoom1: ChatRoom
            beforeEach {
                member = memberRepository.save(createTestMember())
                chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member))
            }
        },
    )
