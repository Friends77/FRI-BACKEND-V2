package com.friends.config

import com.friends.category.entity.Category
import com.friends.category.entity.CategoryType
import com.friends.category.repository.CategoryRepository
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.service.ChatRoomCommandService
import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.service.MessageCommandService
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.random.Random

@Component
class InitDB(
    private val initTestMember: InitTestMember,
    private val initCategory: InitCategory,
    private val initTestUser: InitTestUser,
) {
    @Value("\${spring.jpa.hibernate.ddl-auto}")
    lateinit var ddlAuto: String

    @PostConstruct
    fun init() {
        println(ddlAuto)
        if (ddlAuto != "create") {
            return
        }
        initTestMember.init()
        initCategory.init()
        initTestUser.init()
    }

    @Component
    class InitTestMember(
        private val memberRepository: MemberRepository,
        private val passwordEncoder: PasswordEncoder,
    ) {
        fun init() {
            // 테스트 유저 생성
            memberRepository.saveAll(
                listOf(
                    Member.createUser(
                        nickname = "user",
                        email = "user",
                        password = passwordEncoder.encode("user"),
                    ),
                    Member.createUser(
                        nickname = "user2",
                        email = "user2",
                        password = passwordEncoder.encode("user2"),
                    ),
                    Member.createAdmin(
                        nickname = "admin",
                        email = "admin",
                        password = passwordEncoder.encode("admin"),
                    ),
                ),
            )
        }
    }

    @Component
    class InitCategory(
        private val categoryRepository: CategoryRepository,
    ) {
        fun init() {
            categoryRepository.deleteAll()
            categoryRepository.save(Category(name = "자유수다", type = CategoryType.SUBJECT, image = "🧑‍🧑‍🧒"))
            categoryRepository.save(Category(name = "팬덤", type = CategoryType.SUBJECT, image = "🎈"))
            categoryRepository.save(Category(name = "게임", type = CategoryType.SUBJECT, image = "🎮"))
            categoryRepository.save(Category(name = "음악", type = CategoryType.SUBJECT, image = "🎵"))
            categoryRepository.save(Category(name = "맛집", type = CategoryType.SUBJECT, image = "🍽️"))
            categoryRepository.save(Category(name = "고민/상담", type = CategoryType.SUBJECT, image = "🗣️"))
            categoryRepository.save(Category(name = "엔터테인먼트", type = CategoryType.SUBJECT, image = "🎈"))
            categoryRepository.save(Category(name = "스포츠", type = CategoryType.SUBJECT, image = "🏆"))
            categoryRepository.save(Category(name = "일상", type = CategoryType.SUBJECT, image = "☘️"))
            categoryRepository.save(Category(name = "운동/건강", type = CategoryType.SUBJECT, image = "👟️"))
            categoryRepository.save(Category(name = "여행", type = CategoryType.SUBJECT, image = "✈️"))
            categoryRepository.save(Category(name = "취미", type = CategoryType.SUBJECT, image = "🧸"))
            categoryRepository.save(Category(name = "친목/모임", type = CategoryType.SUBJECT, image = "👏"))
            categoryRepository.save(Category(name = "패션/뷰티", type = CategoryType.SUBJECT, image = "🧢"))
            categoryRepository.save(Category(name = "직업", type = CategoryType.SUBJECT, image = "💼"))
            categoryRepository.save(Category(name = "자기계발", type = CategoryType.SUBJECT, image = "📚"))
            categoryRepository.save(Category(name = "재테크", type = CategoryType.SUBJECT, image = "💰"))
            categoryRepository.save(Category(name = "서울", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "부산", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "대구", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "인천", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "광주", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "대전", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "울산", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "세종", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "경기도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "강원도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "충청북도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "충청남도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "전라북도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "전라남도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "경상북도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "경상남도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "제주도", type = CategoryType.REGION))
        }
    }

    @Component
    class InitTestUser(
        private val categoryRepository: CategoryRepository,
        private val memberRepository: MemberRepository,
        private val passwordEncoder: PasswordEncoder,
        private val profileRepository: ProfileRepository,
        private val profileInterestTagRepository: ProfileInterestTagRepository,
        private val chatRoomCommandService: ChatRoomCommandService,
        private val chatRoomRepository: ChatRoomRepository,
        private val friendshipRepository: FriendShipRepository,
        private val messageCommandService: MessageCommandService,
    ) {
        fun init() {
            // 100 명의 테스트 유저 생성
            val categories = categoryRepository.findAll()
            val members = mutableListOf<Member>()

            for (i in 1..100) {
                val member =
                    memberRepository.save(
                        Member.createUser(
                            nickname = "test$i",
                            email = "test$i@com",
                            password = passwordEncoder.encode("password"),
                        ),
                    )
                members.add(member)

                val randomBirth =
                    LocalDate.of(
                        (1990..1999).random(),
                        (1..12).random(),
                        (1..28).random(),
                    )
                val randomGender = GenderEnum.entries.toTypedArray().random()
                val randomLatitude = 38 + Random.nextDouble(0.0, 1.0)
                val randomLongitude = 127 + Random.nextDouble(0.0, 1.0)
                val randomLocation = Location(randomLatitude, randomLongitude)
                val randomMbti = MbtiEnum.entries.toTypedArray().random()

                val profile =
                    profileRepository.save(
                        Profile(
                            member = member,
                            // 생일은 1990년 1월 1일부터 1999년 12월 31일 사이의 랜덤한 날짜로 설정
                            birth = randomBirth,
                            gender = randomGender,
                            location = randomLocation,
                            mbti = randomMbti,
                        ),
                    )

                // 1~10개의 랜덤한 카테고리를 저장
                val randomCategoryCount = (1..10).random()
                val randomCategories = categories.shuffled().take(randomCategoryCount)

                randomCategories.map {
                    profileInterestTagRepository.save(
                        ProfileInterestTag(
                            profile = profile,
                            category = it,
                        ),
                    )
                }
            }

            // 참여 중인 채팅방 10개 생성
            for (i in 1..20) {
                val randomInt = (3..7).random()
                chatRoomCommandService.createChatRoom(
                    ChatRoomCreateRequestDto(
                        title = "채팅방 $i",
                        categoryIdList =
                            categories
                                .shuffled()
                                .take(randomInt)
                                .map { it.id }
                                .toSet(),
                        "채팅방 $i 입니다.",
                    ),
                    members[0].id,
                    null,
                )
            }
            for (i in 20..30) {
                val randomInt = (3..7).random()
                chatRoomCommandService.createChatRoom(
                    ChatRoomCreateRequestDto(
                        title = "채팅방 $i",
                        categoryIdList =
                            categories
                                .shuffled()
                                .take(randomInt)
                                .map { it.id }
                                .toSet(),
                        null,
                    ),
                    members[1].id,
                    null,
                )
            }

            // 채팅방에 10~20명의 유저를 랜덤하게 입장시킴
            val chatRooms = chatRoomRepository.findAll()
            for (chatRoom in chatRooms) {
                val randomInt = (10..20).random()
                val randomMembers = members.shuffled().take(randomInt)
                for (member in randomMembers) {
                    chatRoomCommandService.enterChatRoom(chatRoom.id, member.id)
                }
            }

            // 랜덤으로 친구 추가
            for (member in members) {
                val randomInt = (1..5).random()
                val randomFriends = members.shuffled().take(randomInt)
                for (friend in randomFriends) {
                    if (member.id == friend.id) {
                        continue
                    }
                    friendshipRepository.save(Friendship(id = 0L, receiver = member, requester = friend, friendshipStatus = FriendshipStatusEnums.ACCEPT))
                }
            }

            messageCommandService.clear()
        }
    }
}
