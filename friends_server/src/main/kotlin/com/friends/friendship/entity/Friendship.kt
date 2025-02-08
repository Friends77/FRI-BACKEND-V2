package com.friends.friendship.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Friendship(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    val id: Long = 0L,
    //내가 친구요청을 한 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_member_id")
    val requester: Member,
    //내가 친구요청을 받은 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_member_id")
    val receiver: Member,
    @Enumerated(EnumType.STRING)
    private var friendshipStatus: FriendshipStatusEnums,
) : BaseModifiableEntity() {
    fun getFriendshipStatus(): FriendshipStatusEnums = friendshipStatus

    fun acceptFriendshipRequest() {
        friendshipStatus = FriendshipStatusEnums.ACCEPT
    }

    fun waitFriendshipRequest() {
        friendshipStatus = FriendshipStatusEnums.WAITING
    }

    fun blockFriendRequest() {
        friendshipStatus = FriendshipStatusEnums.BLOCK
    }
}
