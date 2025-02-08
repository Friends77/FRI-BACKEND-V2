package com.friends.profile

import com.friends.category.entity.Category
import com.friends.createTestCategory
import com.friends.member.MEMBER_ID
import com.friends.member.MEMBER_NICKNAME
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.support.TEST_IMAGE_UPLOAD_FILE_URL
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

val PROFILE_ID = 1L
val PROFILE_IMAGE: MultipartFile =
    MockMultipartFile(
        "file", // 필드 이름
        "profile.img", // 파일 이름
        "image/jpeg", // 콘텐츠 타입
        byteArrayOf(1, 2, 3, 4), // 파일 내용 (예제 데이터)
    )
const val TEST_PROFILE_IMAGE_URL = "test profile image url"
const val TEST_IMAGE_URL = "test imageurl"

fun createTestMember(): Member = Member(id = MEMBER_ID, nickname = "test name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE)

fun createTestProfile(
    member: Member = createTestMember(),
    birth: LocalDate = LocalDate.now(),
    gender: GenderEnum = GenderEnum.MAN,
    imageUrl: String = TEST_IMAGE_URL,
): Profile = Profile(member = member, birth = birth, gender = gender, mbti = MbtiEnum.ENFJ, location = Location(10.0, 10.0), selfDescription = "test self description", imageUrl = imageUrl)

fun createTestProfileResponseDto(): ProfileResponseDto = ProfileResponseDto(memberId = createTestMember().id, nickname = createTestMember().nickname, email = "test@test.com", birth = LocalDate.now(), gender = GenderEnum.MAN, location = Location(10.0, 10.0), selfDescription = "test self description", imageUrl = "test imageurl", mbti = MbtiEnum.ENFJ)

fun createTestProfileCreateDto(): ProfileCreateDto = ProfileCreateDto(birth = LocalDate.now(), gender = GenderEnum.MAN, location = Location(10.0, 10.0), selfDescription = "test self description", imageUrl = TEST_IMAGE_UPLOAD_FILE_URL)

fun updateTestProfile(
    imageUrl: String? = "test update imageurl",
): ProfileUpdateDto = ProfileUpdateDto(nickname = MEMBER_NICKNAME, birth = LocalDate.now(), gender = GenderEnum.WOMAN, location = Location(20.0, 20.0), selfDescription = "test update self description", mbti = MbtiEnum.ENTJ, imageUrl = imageUrl)

fun createTestProfileInterestTag(
    profile: Profile = createTestProfile(),
    category: Category = createTestCategory(),
) = ProfileInterestTag(profile = profile, category = category)
