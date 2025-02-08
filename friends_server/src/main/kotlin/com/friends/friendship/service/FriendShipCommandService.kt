package com.friends.friendship.service

import com.friends.alarm.entity.AlarmType
import com.friends.alarm.service.AlarmCommandService
import com.friends.friendship.dto.FriendShipReceiveDto
import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.exception.FriendShipAlreadyExistException
import com.friends.friendship.exception.FriendShipBlockedException
import com.friends.friendship.exception.FriendShipNotFoundException
import com.friends.friendship.exception.FriendShipNotWaitingException
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendShipCommandService(
    private val friendShipRepository: FriendShipRepository,
    private val alarmCommandService: AlarmCommandService,
    private val memberRepository: MemberRepository,
) {
    /**
     * 친구 요청을 보냅니다.
     * 요청을 받은 사람은 알림을 받습니다.
     */
    fun requestFriendship(
        requesterId: Long,
        receiverId: Long,
    ) {
        val requester = memberRepository.findById(requesterId).orElseThrow { MemberNotFoundException() }
        val receiver = memberRepository.findById(receiverId).orElseThrow { MemberNotFoundException() }

        // 이미 요청한 친구인지, 차단된 친구인지 확인합니다.
        friendShipRepository.findByRequesterAndReceiver(requester, receiver)?.let {
            if (it.getFriendshipStatus() == FriendshipStatusEnums.BLOCK) {
                throw FriendShipBlockedException()
            }
            throw FriendShipAlreadyExistException()
        }
        friendShipRepository.findByRequesterAndReceiver(receiver, requester)?.let {
            if (it.getFriendshipStatus() == FriendshipStatusEnums.BLOCK) {
                throw FriendShipBlockedException()
            }
            throw FriendShipAlreadyExistException()
        }

        friendShipRepository.save(Friendship(requester = requester, receiver = receiver, friendshipStatus = FriendshipStatusEnums.WAITING))
        alarmCommandService.sendFriendRequestAlarm(requesterId, receiverId)
    }

    /**
     * 친구 요청을 수락합니다.
     * 수락된 친구에 대해서는 친구 리스트에서 조회됩니다.
     */
    fun acceptFriendship(
        receiverId: Long,
        friendShipReceiveDto: FriendShipReceiveDto,
    ) {
        val requesterId = friendShipReceiveDto.requesterId
        val alarmId = friendShipReceiveDto.alarmId

        val friendship =
            friendShipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                ?: throw FriendShipNotFoundException()

        if (friendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
            friendship.acceptFriendshipRequest()
            alarmCommandService.changeAlarmType(alarmId, AlarmType.FRIEND_REQUEST_ACCEPTED)
        } else {
            throw FriendShipNotWaitingException()
        }
    }

    /**
     * 친구 요청을 거절합니다.
     * 다시 요청할 수 있습니다.
     */
    fun rejectFriendship(
        receiverId: Long,
        friendShipReceiveDto: FriendShipReceiveDto,
    ) {
        val requesterId = friendShipReceiveDto.requesterId
        val alarmId = friendShipReceiveDto.alarmId

        val friendship =
            friendShipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                ?: throw FriendShipNotFoundException()

        if (friendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
            friendShipRepository.delete(friendship)
            alarmCommandService.changeAlarmType(alarmId, AlarmType.FRIEND_REQUEST_REJECTED)
        } else {
            throw FriendShipNotWaitingException()
        }
    }

    /**
     * 친구 요청을 차단합니다.
     * 차단된 친구는 친구 리스트에서 조회되지 않고 다시 요청할 수 없습니다.
     */
    fun blockFriendship(
        receiverId: Long,
        friendShipReceiveDto: FriendShipReceiveDto,
    ) {
        val requesterId = friendShipReceiveDto.requesterId
        val alarmId = friendShipReceiveDto.alarmId

        val friendship =
            friendShipRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                ?: throw FriendShipNotFoundException()

        if (friendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
            friendship.blockFriendRequest()
            alarmCommandService.changeAlarmType(alarmId, AlarmType.FRIEND_REQUEST_BLOCKED)
        } else {
            throw FriendShipNotWaitingException()
        }
    }
}
