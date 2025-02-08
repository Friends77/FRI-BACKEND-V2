package com.friends.aop

import com.friends.common.annotation.DistributedLock
import com.friends.common.exception.InvalidRedisLockIdentifierException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Service

@SpringBootTest
class DistributedLockAopTest(
    private val likeService: ChatRoomLikeService,
) : BehaviorSpec({
        given("체팅방 좋아요 감소 기능에서") {
            val coroutinesCount = 1000
            val likeCount = 1000
            beforeEach {
                likeService.likeCount = likeCount
            }

            `when`("동시에 좋아요를 증가시키면") {
                val expectedLikeCount = likeCount + coroutinesCount
                then("좋아요가 2000개가 아닌 값이 나올 수 있다.") {
                    runBlocking {
                        val jobs =
                            List(coroutinesCount) {
                                launch(Dispatchers.IO) {
                                    likeService.increaseLike(1L)
                                }
                            }

                        jobs.joinAll()
                        println(likeService.likeCount)// 테스트 결과 : 1회차 : 1989 / 2회차 : 1987
                        likeCount shouldNotBe expectedLikeCount
                    }
                }
            }

            `when`("동시에 좋아요를 감소시키면") {
                val expectedLikeCount = likeCount - coroutinesCount
                then("좋아요가 0개 남는다.") {
                    runBlocking {
                        val jobs =
                            List(coroutinesCount) {
                                launch(Dispatchers.IO) {
                                    likeService.decreaseLike(1L)
                                }
                            }

                        jobs.joinAll()
                        println(likeService.likeCount)//테스트 결과 : 1회차 : 0 / 2회차 : 0
                        likeService.likeCount shouldBe expectedLikeCount
                    }
                }
            }

            `when`("잘못된 식별자를 사용하여 좋아요를 증가시키면") {
                then("예외가 발생한다.") {
                    runBlocking {
                        val jobs =
                            List(coroutinesCount) {
                                launch(Dispatchers.IO) {
                                    shouldThrow<InvalidRedisLockIdentifierException> { likeService.increaseLikeCount(10L) }
                                }
                            }
                        jobs.joinAll()
                    }
                }
            }
        }
    }) {
    @Service
    class ChatRoomLikeService(
        var likeCount: Int = 1000,
    ) {
        @DistributedLock(
            lockName = "LIKE-LOCK",
            identifier = "id",
        )
        fun decreaseLike(id: Long) {
            likeCount -= 1
        }

        fun increaseLike(id: Long) {
            likeCount += 1
        }

        @DistributedLock(
            lockName = "LIKE-LOCK",
            identifier = "id",
        )
        fun increaseLikeCount(likeId: Long) {
            likeCount += 1
        }
    }
}
