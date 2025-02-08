package com.friends.friendship.entity

enum class FriendshipStatusEnums(
    val description: String,
) {
    ACCEPT("친구요청을 수락했습니다"),
    WAITING("요청 대기중인 상태입니다"),
    BLOCK("친구요청을 거절당했습니다"),
}

enum class FriendshipRequestStatusEnums(
    val description: String,
) {
    AVAILABLE("친구 신청 가능 상태입니다"),
    RECEIVED("상대가 나에게 친구 요청함"),
    REQUESTED("내가 상대에게 친구 요청함"),
    UNAVAILABLE("친구 신청 불가능 상태입니다(이미 친구, 차단, 자기 자신)"),
}
