package com.friends.chat.controller

import com.friends.chat.TEST_CHAT_ROOM_ID
import com.friends.chat.createTestToggleLikeResponseDto
import com.friends.chat.service.ChatRoomLikeCommandService
import com.friends.support.annotation.ControllerTest
import com.friends.support.postWithAuthentication
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerTest
@WebMvcTest(ChatRoomLikeController::class)
class ChatRoomLikeControllerTest(
    @MockkBean
    private val chatRoomLikeCommandService: ChatRoomLikeCommandService,
    private val mockMvc: MockMvc,
) : BehaviorSpec({
        val requestPath = "/api/user/chat/room"
        given("POST $requestPath/{chatRoomId}/like Test") {
            `when`("정상적인 요청이 들어올 경우") {
                every { chatRoomLikeCommandService.toggleLike(any(), any()) } returns createTestToggleLikeResponseDto()
                then("채팅방 좋아요 토글한다.") {
                    mockMvc
                        .perform(
                            postWithAuthentication("$requestPath/$TEST_CHAT_ROOM_ID/like"),
                        ).andExpect(
                            status().isOk,
                        )
                }
            }

            `when`("채팅방 ID가 양수가 아닌 경우") {
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            postWithAuthentication("$requestPath/0/like"),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }
        }
    })
