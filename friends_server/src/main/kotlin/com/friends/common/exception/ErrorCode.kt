package com.friends.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: Int,
    val errorMessage: String,
) {
    // global error
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, -10000, "적절하지 않은 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -10001, "서버 내부 오류입니다."),
    INVALID_SIZE(HttpStatus.BAD_REQUEST, -10002, "size는 양수여야 합니다."),

    // Auth API error 11000대
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, -11001, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "유효하지 않은 Refresh Token입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11003, "유효하지 않은 Access Token입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "RefreshToken 이 존재하지 않습니다."),
    MISSING_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11003, "SocialAccessToken 이 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, -11004, "존재하지 않는 이메일입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, -11005, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, -11006, "유효하지 않은 비밀번호입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, -11007, "유효하지 않은 닉네임입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, -11008, "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, -11009, "권한이 없습니다."),
    DUPLICATE_NEW_PASSWORD(HttpStatus.BAD_REQUEST, -11010, "새 비밀번호가 이전 비밀번호와 동일합니다."),
    OAUTH2_RESET_PASSWORD(HttpStatus.BAD_REQUEST, -11011, "OAuth2로 가입한 계정은 비밀번호를 변경할 수 없습니다."),

    // Member API error 12000대
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, -12001, "존재하지 않는 회원입니다."),

    // Email API error 13000대
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, -13001, "유효하지 않은 이메일입니다."),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.UNAUTHORIZED, -13002, "유효하지 않은 이메일 인증 코드입니다."),
    SMTP_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, -13003, "이메일 서버와의 연결에 실패했습니다."),

    // OAuth2 API error 14000대
    OAUTH2_ACCESS_TOKEN_FETCH_FAILED(HttpStatus.UNAUTHORIZED, -14001, "OAuth2 Access Token 획득에 실패했습니다."),
    OAUTH2_USER_INFO_FETCH_FAILED(HttpStatus.UNAUTHORIZED, -14002, "OAuth2 User 정보 획득에 실패했습니다."),
    OAUTH2_NULL_RESPONSE(HttpStatus.BAD_GATEWAY, -14003, "OAuth2 API로부터 응답이 없습니다."),

    // Profile API error 15000대
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, -15001, "해당 멤버의 프로필이 존재하지 않습니다."),
    PROFILE_LOCATION_NULL(HttpStatus.NOT_FOUND, -15002, "프로필 위치 정보가 존재하지 않습니다."),
    PROFILE_URL_NOT_BLANK(HttpStatus.BAD_REQUEST, -15003, "프로필 이미지 URL은 공백일 수 없습니다."),

    // Board API error 16000대
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, -16001, "존재하지 않는 게시물입니다."),
    INVALID_BOARD_ACCESS(HttpStatus.FORBIDDEN, -16002, "게시글에 대한 유효하지 않은 접근입니다."),
    BOARD_LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, -16003, "이미 좋아요를 눌렀습니다."),
    BOARD_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, -16004, "해당 게시글에 대한 좋아요가 존재하지 않습니다."),
    BOARD_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, -16005, "전부 존재하지 않는 게시판 카테고리입니다."),

    // Chat API error 17000대
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, -17001, "존재하지 않는 채팅방입니다."),
    CHAT_ROOM_TITLE_BLANK(HttpStatus.BAD_REQUEST, -17002, "채팅방 제목은 공백일 수 없습니다."),
    CHAT_ROOM_CATEGORY_INVALID_SIZE(HttpStatus.BAD_REQUEST, -17004, "채팅방 카테고리는 최소 1개 이상 선택해주세요."),
    CHAT_ROOM_POSITIVE_LIKE_COUNT(HttpStatus.BAD_REQUEST, -17005, "채팅방 좋아요 수는 0 이상이어야 합니다."),
    CHAT_ROOM_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, -17006, "전부 존재하지 않는 채팅방 카테고리입니다."),
    INVALID_LAST_CHAT_ROOM_ID(HttpStatus.BAD_REQUEST, -17007, "lastChatRoomMemberId는 양수여야 합니다."),
    INVALID_CHAT_ROOM_ID(HttpStatus.BAD_REQUEST, -17008, "채팅방 ID는 양수여야 합니다."),
    CHAT_ROOM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, -17009, "채팅방에 존재하지 않는 멤버입니다."),
    UNEXPECTED_CHAT_ROOM(HttpStatus.INTERNAL_SERVER_ERROR, -17010, "예상치 못한 채팅방 오류입니다."),
    NOT_A_MEMBER_OF_CHAT_ROOM(HttpStatus.FORBIDDEN, -17011, "참여중인 채팅방이 아닙니다."),
    NOT_CHAT_ROOM_MANAGER(HttpStatus.FORBIDDEN, -17012, "채팅방 관리자가 아닙니다."),
    CHAT_ROOM_UPDATE_NOTHING(HttpStatus.BAD_REQUEST, -17013, "이전과 동일하여 수정할 내용이 없습니다."),
    CHAT_ROOM_BASE_IMAGE_CANNOT_DELETE(HttpStatus.BAD_REQUEST, -17014, "기본 이미지는 삭제할 수 없습니다."),
    NOT_FORCE_LEAVE_YOURSELF(HttpStatus.FORBIDDEN, -17015, "자신을 강제로 퇴장시킬 수 없습니다."),
    NOT_BLANK_CHAT_ROOM_DESCRIPTION(HttpStatus.BAD_REQUEST, -17016, "채팅방 설명은 공백일 수 없습니다."),

    // Comment API error 18000대
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, -18001, "존재하지 않는 댓글입니다."),
    INVALID_COMMENT_ACCESS(HttpStatus.FORBIDDEN, -18002, "댓글에 대한 유효하지 않은 접근입니다."),

    // category API error 19000대
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, -19001, "존재하지 않는 카테고리입니다."),

    // Vote API error 20000대
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, -20001, "존재하지 않는 투표입니다."),
    VOTE_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, -20002, "존재하지 않는 선택지입니다."),
    VOTE_OPTION_POSITIVE_COUNT(HttpStatus.BAD_REQUEST, -20003, "투표수는 0 이상이어야 합니다."),
    VOTE_NOT_FOUND_IN_BOARD(HttpStatus.NOT_FOUND, -20004, "해당 게시글에 투표가 존재하지 않습니다."),

    // Message API error 21000대
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, -21001, "존재하지 않는 메시지입니다."),
    NOT_MESSAGE_SENDER(HttpStatus.FORBIDDEN, -21002, "메세지 발신자가 아닙니다."),

    // Friendship API error 22000대
    FRIENDSHIP_ALREADY_EXIST(HttpStatus.CONFLICT, -22001, "이미 친구이거나, 친구 요청을 보낸 상태입니다."),
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, -22002, "친구 관계나 요청 상태가 존재하지 않습니다."),
    FRIENDSHIP_NOT_WAITING(HttpStatus.BAD_REQUEST, -22003, "친구 요청 대기 상태가 아닙니다."),
    FRIENDSHIP_BLOCKED(HttpStatus.FORBIDDEN, -22004, "친구 요청이 차단되었습니다."),

    // Alarm API error 23000대
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, -23001, "존재하지 않는 알람입니다."),

    // Search API error 24000대
    INVALID_SEARCH_NICKNAME(HttpStatus.BAD_REQUEST, -24001, "검색할 닉네임은 공백일 수 없습니다."),

    // Redis API error 25000대
    REDISSON_LOCK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -25001, "레디스 락을 사용할 수 없습니다."),
    INVALID_REDISSON_IDENTIFIER(HttpStatus.INTERNAL_SERVER_ERROR, -25002, "레디스 락 식별자가 잘못되었습니다."),

    // Image API error 26000대
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, -26001, "유효하지 않은 이미지 URL입니다."),
}
