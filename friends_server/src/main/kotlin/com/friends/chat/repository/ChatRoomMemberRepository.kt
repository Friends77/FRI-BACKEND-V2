package com.friends.chat.repository

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.util.getLimitList
import com.friends.common.util.getList
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomMemberRepository :
    JpaRepository<ChatRoomMember, Long>,
    ChatRoomMemberCustomRepository {
    fun countByChatRoom(chatRoom: ChatRoom): Int

    fun findAllByMemberId(memberId: Long): List<ChatRoomMember>

    fun findByChatRoomAndMember(
        chatRoom: ChatRoom,
        member: Member,
    ): ChatRoomMember?

    fun existsChatRoomMemberByChatRoomAndMember(
        chatRoom: ChatRoom,
        member: Member,
    ): Boolean

    fun findFirstByChatRoomOrderByCreatedAt(
        chatRoom: ChatRoom,
    ): ChatRoomMember

    fun findByChatRoom(chatRoom: ChatRoom): List<ChatRoomMember>

    fun findAllByChatRoomId(chatRoomId: Long): List<ChatRoomMember>
}

interface ChatRoomMemberCustomRepository {
    fun findAllByMemberAndFriends(
        memberId: Long,
        memberList: List<Member>?,
    ): List<ChatRoomMember>

    fun findRepresentativeProfileByChatRoomId(chatRoomId: Long): List<Member>

    fun findMemberByChatRoomAndMemberExceptManager(
        chatRoom: ChatRoom,
        member: Member,
        manager: Member?,
    ): List<Member>
}

class ChatRoomMemberCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : ChatRoomMemberCustomRepository {
    override fun findAllByMemberAndFriends(
        memberId: Long,
        memberList: List<Member>?,
    ): List<ChatRoomMember> =
        kotlinJdslJpqlExecutor.getList {
            select(entity(ChatRoomMember::class)) // 중복 제거
                .from(entity(ChatRoomMember::class), join(ChatRoomMember::chatRoom))
                .where(
                    and(
                        path(ChatRoomMember::member).path(Member::id).eq(memberId), // 내가 속한 채팅방
                        dynamicChatRoomList(memberList), // 친구들이 속한 채팅방이어야한다는 조건
                    ),
                ).orderBy(path(ChatRoomMember::id).desc())
        }

    override fun findRepresentativeProfileByChatRoomId(chatRoomId: Long): List<Member> =
        kotlinJdslJpqlExecutor.getLimitList(0, 4) {
            select(path(ChatRoomMember::member))
                .from(entity(ChatRoomMember::class), join(ChatRoomMember::member))
                .where(path(ChatRoomMember::chatRoom).path(ChatRoom::id).eq(chatRoomId))
                .orderBy(path(ChatRoomMember::id).asc())
        }

    override fun findMemberByChatRoomAndMemberExceptManager(
        chatRoom: ChatRoom,
        member: Member,
        manager: Member?,
    ): List<Member> =
        kotlinJdslJpqlExecutor.getList {
            select(path(ChatRoomMember::member))
                .from(entity(ChatRoomMember::class), join(ChatRoomMember::member))
                .where(
                    and(
                        path(ChatRoomMember::chatRoom).eq(chatRoom),
                        path(ChatRoomMember::member).ne(member),
                        manager?.let { path(ChatRoomMember::member).ne(it) },
                    ),
                ).orderBy(path(ChatRoomMember::member).path(Member::nickname).asc())
        }

    private fun Jpql.dynamicChatRoomList(
        memberList: List<Member>?,
    ): Predicate? =
        if (memberList == null) {
            null
        } else {
            path(ChatRoomMember::chatRoom).`in`( // 해당 채팅방이 친구가 속한 채팅방인지 확인
                kotlinJdslJpqlExecutor.getList {
                    // 출력 : 친구들이 속한 채팅방 리스트
                    selectDistinct(path(ChatRoomMember::chatRoom)) // 중복 채팅방 제거
                        .from(entity(ChatRoomMember::class), join(ChatRoomMember::chatRoom))
                        .where(path(ChatRoomMember::member).`in`(memberList))
                },
            )
        }
}
