package com.friends.chat.service

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.GenderRatioEnum
import com.friends.chat.repository.ChatRoomMemberAgeRangeRepository
import com.friends.chat.repository.ChatRoomMemberGenderRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.profile.entity.GenderEnum
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.TreeMap

@Service
class ChatRoomRecommendationCriteriaService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val chatRoomMemberAgeRangeRepository: ChatRoomMemberAgeRangeRepository,
    private val chatRoomMemberGenderRepository: ChatRoomMemberGenderRepository,
) {
    @Scheduled(cron = "0 0 5 * * *")
    @Transactional(readOnly = true)
    fun extractMemberMostAgeRange() {
        val chatRooms = chatRoomRepository.findAll()
        val currentYear = LocalDate.now().year

        chatRooms.forEach { chatRoom ->
            val ageRangeCountMap = getAgeRangeCountMap(chatRoom, currentYear)
            saveMostCommonAgeRanges(chatRoom.id, ageRangeCountMap)
        }
    }

    // 특정 채팅방 멤버들의 나이대 카운트를 계산하는 함수
    private fun getAgeRangeCountMap(
        chatRoom: ChatRoom,
        currentYear: Int,
    ): Map<Int, Int> {
        val chatRoomMembers = chatRoomMemberRepository.findByChatRoom(chatRoom)
        val ageRangeCountMap = TreeMap<Int, Int>()

        chatRoomMembers.forEach { chatRoomMember ->
            chatRoomMember.member.profile?.birth?.let { birth ->
                val age = calculateAge(birth, currentYear)
                val ageRange = age / 10 * 10
                ageRangeCountMap[ageRange] = ageRangeCountMap.getOrDefault(ageRange, 0) + 1
            }
        }
        return ageRangeCountMap
    }

    // 나이를 계산하는 함수
    private fun calculateAge(
        birth: LocalDate,
        currentYear: Int,
    ): Int {
        val now = LocalDate.now()
        return if (now.isAfter(birth.withYear(currentYear))) {
            currentYear - birth.year
        } else {
            currentYear - birth.year - 1
        }.coerceAtLeast(10) // 나이가 10세 미만인 경우 10세로 고정
            .coerceAtMost(60)// 나이가 60세 이상인 경우 60세로 고정
    }

    // 가장 흔한 나이대를 저장하는 함수
    private fun saveMostCommonAgeRanges(
        chatRoomId: Long,
        ageRangeCountMap: Map<Int, Int>,
    ) {
        val maxCount = ageRangeCountMap.maxByOrNull { it.value }?.key
        if (maxCount != null) {
            chatRoomMemberAgeRangeRepository.save(chatRoomId, maxCount.toString())
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional(readOnly = true)
    fun extractMemberMostGender() {
        val chatRooms = chatRoomRepository.findAll()
        chatRooms.forEach { chatRoom ->
            val genderCountMap = getGenderCountMap(chatRoom)
            determineMostGender(genderCountMap)?.also { chatRoomMemberGenderRepository.save(chatRoom.id, it) }
        }
    }

    private fun getGenderCountMap(chatRoom: ChatRoom): Map<GenderEnum?, Int> {
        val chatRoomMembers = chatRoomMemberRepository.findByChatRoom(chatRoom)
        return chatRoomMembers
            .filter { it.member.profile?.gender == GenderEnum.MAN || it.member.profile?.gender == GenderEnum.WOMAN }
            .groupingBy { it.member.profile?.gender }
            .eachCount()
    }

    private fun determineMostGender(genderCountMap: Map<GenderEnum?, Int>): String? {
        val manCount = genderCountMap.getOrDefault(GenderEnum.MAN, 0)
        val womanCount = genderCountMap.getOrDefault(GenderEnum.WOMAN, 0)
        return when {
            manCount == 0 && womanCount == 0 -> null
            manCount > womanCount -> GenderRatioEnum.MAJORITY_MAN.toString()
            womanCount > manCount -> GenderRatioEnum.MAJORITY_WOMAN.toString()
            else -> GenderRatioEnum.EQUAL.toString()
        }
    }
}
