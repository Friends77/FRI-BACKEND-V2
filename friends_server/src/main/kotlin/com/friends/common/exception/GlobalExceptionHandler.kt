package com.friends.common.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.friends.alarm.AlarmException
import com.friends.board.exception.BoardException
import com.friends.board.exception.CommentException
import com.friends.board.exception.VoteException
import com.friends.category.CategoryException
import com.friends.chat.ChatException
import com.friends.email.EmailException
import com.friends.friendship.exception.FriendShipException
import com.friends.image.ImageException
import com.friends.member.MemberExceptions
import com.friends.message.MessageException
import com.friends.oauth2.OAuth2Exception
import com.friends.profile.ProfileExceptions
import com.friends.security.securityException.AuthenticationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice // @ControllerAdvice와 @ResponseBody를 결합한 것
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(//메소드 인자 유효성 검사 실패 시 발생하는 예외 처리
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error(ex)
        return getInvalidRequestResponse(ex.messages().joinToString())
    }

    override fun handleHttpMessageNotReadable( //JSON 요청이 잘못된 경우 발생하는 예외 처리(예: JSON 형식이 잘못된 경우)
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorMessage =
            when (val cause = ex.cause) {
                is InvalidFormatException -> "${cause.path.joinToString(separator = ".") { it?.fieldName.orEmpty() }}: ${ex.message}" // 유효성 검사 실패 시 발생하는 예외 처리(예: JSON 형식이 잘못된 경우)
                is MismatchedInputException -> {
                    "${cause.path.joinToString(separator = ".") { it?.fieldName.orEmpty() }}: ${ex.message}" // 입력 값이 예상과 다른 경우 발생하는 예외 처리(예: 문자열을 숫자로 변환할 수 없는 경우)
                }
                else -> "유효하지 않은 요청입니다"
            }
        logger.error(ex)
        return getInvalidRequestResponse(errorMessage)
    }

    override fun handleHttpRequestMethodNotSupported( //지원되지 않는 HTTP 메소드 요청 시 발생하는 예외 처리 (예: GET 요청을 POST로 보낼 때)
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error(ex)
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleMissingServletRequestPart( //필수 요청 파라미터 누락 시 발생하는 예외 처리(예: required = true로 설정된 파라미터가 없는 경우)
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error(ex)
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleMissingServletRequestParameter( //multipart/form-data 요청 필수 부분 누락 시 발생하는 예외 처리
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error(ex)
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleMissingPathVariable(//필수 경로 변수 누락 시 발생하는 예외 처리
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error(ex)
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleHttpMediaTypeNotSupported(// 서버가 지원하지 않는 미디어 타입 요청 시 발생하는 예외 처리
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error(ex)
        return getInvalidRequestResponse(ex.message, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    override fun handleHandlerMethodValidationException(//@Valid 유효성 검사 실패 시 발생하는 예외 처리
        ex: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? = getInvalidRequestResponse(ex.messages().joinToString())

    @ExceptionHandler(AuthenticationException::class)
    fun handleInvalidJwtException(ex: AuthenticationException): ResponseEntity<Any> {
        logger.error("Invalid JWT", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(EmailException::class)
    fun handleEmailException(ex: EmailException): ResponseEntity<Any> {
        logger.error("Email Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(OAuth2Exception::class)
    fun handleOAuth2Exception(ex: OAuth2Exception): ResponseEntity<Any> {
        logger.error("OAuth2 Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(BoardException::class)
    fun handleBoardException(ex: BoardException): ResponseEntity<Any> {
        logger.error("Board Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(ProfileExceptions::class)
    fun handleProfileException(ex: ProfileExceptions): ResponseEntity<Any> {
        logger.error("Profile Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(MemberExceptions::class)
    fun handleMemberException(ex: MemberExceptions): ResponseEntity<Any> {
        logger.error("Member Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(ChatException::class)
    fun handleChatException(ex: ChatException): ResponseEntity<Any> {
        logger.error("Chat Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(CommentException::class)
    fun handleCommentException(ex: CommentException): ResponseEntity<Any> {
        logger.error("Comment Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(CategoryException::class)
    fun handleCategoryException(ex: CategoryException): ResponseEntity<Any> {
        logger.error("Category Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(MessageException::class)
    fun handleMessageException(ex: MessageException): ResponseEntity<Any> {
        logger.error("Message Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(VoteException::class)
    fun handleVoteException(ex: VoteException): ResponseEntity<Any> {
        logger.error("Vote Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(AlarmException::class)
    fun handleAlarmException(ex: AlarmException): ResponseEntity<Any> {
        logger.error("Alarm Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(FriendShipException::class)
    fun handleFriendShipException(ex: FriendShipException): ResponseEntity<Any> {
        logger.error("FriendShip Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(RedisLockException::class)
    fun handleRedisLockException(ex: RedisLockException): ResponseEntity<Any> {
        logger.error("Redis Lock Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(ImageException::class)
    fun handleImageException(ex: ImageException): ResponseEntity<Any> {
        logger.error("Image Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(ParameterValidationException::class)
    fun handleParameterValidationException(ex: ParameterValidationException): ResponseEntity<Any> {
        logger.error("Parameter Validation Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    private fun MethodArgumentNotValidException.messages(): List<String> = bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage.orEmpty()}" } // 필드 이름과 기본 메세지 반환

    private fun HandlerMethodValidationException.messages(): List<String> =
        this.allErrors.map {
            it.defaultMessage.orEmpty() // 기본 메세지 그대로 반환
        }

    private fun getInvalidRequestResponse( // 기본 틀
        errorMessage: String?,
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    ): ResponseEntity<Any> {
        val invalidRequestErrorCode = ErrorCode.INVALID_REQUEST
        return ResponseEntity
            .status(httpStatus)
            .body(ErrorResponse.of(invalidRequestErrorCode, errorMessage))
    }
}
