package com.friends.security.service

import com.friends.category.CategoryNotFoundException
import com.friends.category.repository.CategoryRepository
import com.friends.config.AuthProperties
import com.friends.image.S3ClientService
import com.friends.jwt.AtRtService
import com.friends.jwt.JwtService
import com.friends.jwt.JwtType
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.member.repository.MemberRepository
import com.friends.oauth2.OAuth2Service
import com.friends.profile.entity.Location
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import com.friends.security.AtRtDto
import com.friends.security.CheckEmailResponseDto
import com.friends.security.CheckNicknameResponseDto
import com.friends.security.OAuth2LoginDto
import com.friends.security.RegisterRequestDto
import com.friends.security.securityException.DuplicateNewPasswordException
import com.friends.security.securityException.EmailDuplicateException
import com.friends.security.securityException.EmailNotFoundException
import com.friends.security.securityException.InvalidNicknameException
import com.friends.security.securityException.InvalidPasswordException
import com.friends.security.securityException.InvalidRefreshTokenException
import com.friends.security.securityException.InvalidTokenException
import com.friends.security.userDetails.CustomUserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val atRtService: AtRtService,
    private val jwtService: JwtService,
    private val authProperties: AuthProperties,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val oAuth2Service: OAuth2Service,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
    private val categoryRepository: CategoryRepository,
    private val profileRepository: ProfileRepository,
    private val s3ClientService: S3ClientService,
) {
    @Transactional(readOnly = true)
    fun login(
        email: String,
        password: String,
    ): AtRtDto {
        val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val userDetails = authenticate.principal as CustomUserDetails
        return atRtService.createAtRt(userDetails.memberId, userDetails.authorities)
    }

    fun refresh(refreshToken: String): AtRtDto {
        if (!atRtService.validateRefreshToken(refreshToken)) {
            throw InvalidRefreshTokenException()
        }

        // refresh 될 때 기존의 access token 과 refresh token 을 삭제합니다.
        atRtService.deleteRefreshToken(refreshToken)
        atRtService.getAccessToken(refreshToken)?.apply {
            atRtService.deleteAccessToken(this)
        }

        val memberId = atRtService.getMemberId(refreshToken)
        val authorities = atRtService.getAuthorities(refreshToken)
        return atRtService.createAtRt(memberId, authorities)
    }

    @Transactional
    fun register(
        registerRequestDto: RegisterRequestDto,
        profileImage: MultipartFile?,
    ) {
        // emailAuthToken 검증
        if (!jwtService.validate(registerRequestDto.authToken)) {
            throw InvalidTokenException()
        }

        // Email-Password 회원가입인지, OAuth2 회원가입인지 확인
        val type = jwtService.getClaim(registerRequestDto.authToken, "type", String::class.java)?.let { JwtType.valueOf(it) } ?: throw InvalidTokenException()
        val user =
            when (type) {
                JwtType.EMAIL -> registerByEmail(registerRequestDto)
                JwtType.OAUTH2 -> registerByOAuth2(registerRequestDto)
            }

        // 이메일이 중복되는지 검증
        if (!validateEmail(user.email).isValid) {
            throw EmailDuplicateException()
        }

        // 닉네임이 중복되는지 검증
        if (!validateNickname(user.nickname).isValid) {
            throw InvalidNicknameException()
        }

        // 유효성 검사를 통과한 멤버 저장
        memberRepository.save(user)

        // 프로필 생성
        val profile =
            Profile(
                member = user,
                imageUrl = profileImage?.let { s3ClientService.upload(it) },
                gender = registerRequestDto.gender,
                birth = registerRequestDto.birth,
                location = registerRequestDto.location?.let { Location(it.latitude, it.longitude) },
                selfDescription = registerRequestDto.selfDescription,
                mbti = registerRequestDto.mbti,
            )
        profileRepository.save(profile)

        // 프로필 관심사 태그 생성
        profileInterestTagRepository.saveAll(
            registerRequestDto.interestTag.map {
                ProfileInterestTag(profile = profile, category = categoryRepository.findById(it).orElseThrow { CategoryNotFoundException() })
            },
        )
    }

    private fun registerByOAuth2(registerRequestDto: RegisterRequestDto): Member {
        val email = jwtService.getClaim(registerRequestDto.authToken, "email", String::class.java) ?: throw InvalidTokenException()
        val provider = jwtService.getClaim(registerRequestDto.authToken, "provider", String::class.java)?.let { OAuth2Provider.valueOf(it) } ?: throw InvalidTokenException()
        val nickname = registerRequestDto.nickname
        return Member.createUser(
            nickname = nickname,
            email = email,
            oauth2Provider = provider,
        )
    }

    private fun registerByEmail(registerRequestDto: RegisterRequestDto): Member {
        val email = jwtService.getClaim(registerRequestDto.authToken, "email", String::class.java) ?: throw InvalidTokenException()
        val nickname = registerRequestDto.nickname
        val password = registerRequestDto.password
        // 패스워드 유효성 검사
        if (!validatePassword(password!!)) {
            throw InvalidPasswordException()
        }
        return Member.createUser(
            nickname = nickname,
            email = email,
            password = passwordEncoder.encode(password),
        )
    }

    fun validatePassword(password: String): Boolean {
        val lengthRegex = Regex(".{8,20}") // 길이 제한
        val lowerCaseRegex = Regex(".*[a-z].*") // 소문자 포함
        val digitRegex = Regex(".*[0-9].*") // 숫자 포함
        val specialCharRegex = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*") // 특수문자 포함
        val noWhiteSpaceRegex = Regex("^[^\\s]*\$") // 공백 금지

        return lengthRegex.matches(password) &&
            lowerCaseRegex.containsMatchIn(password) &&
            digitRegex.containsMatchIn(password) &&
            specialCharRegex.containsMatchIn(password) &&
            noWhiteSpaceRegex.matches(password)
    }

    fun validateNickname(nickname: String): CheckNicknameResponseDto {
        val lengthRegex = Regex("^[가-힣a-zA-Z0-9]{2,20}\$") // 한글, 숫자, 영문 포함 2~20자

        return if (memberRepository.existsByNickname(nickname)) {
            CheckNicknameResponseDto(false, "이미 사용 중인 닉네임입니다.")
        } else if (!lengthRegex.matches(nickname)) {
            CheckNicknameResponseDto(false, "2~20자의 한글, 영문, 숫자만 사용 가능합니다.")
        } else {
            CheckNicknameResponseDto(true, "사용 가능한 닉네임입니다.")
        }
    }

    fun validateEmail(email: String): CheckEmailResponseDto =
        if (memberRepository.existsByEmail(email)) {
            CheckEmailResponseDto(false, "이미 사용 중인 이메일입니다.")
        } else {
            CheckEmailResponseDto(true, "사용 가능한 이메일입니다.")
        }

    fun loginByOAuth2(
        code: String,
        oAuth2Provider: OAuth2Provider,
    ): OAuth2LoginDto {
        val userProfile = oAuth2Service.getUserProfile(code, oAuth2Provider)

        var user = memberRepository.findByEmail(userProfile.email)
        // 이미 가입된 사용자인 경우
        if (user != null) {
            if (user.oauth2Provider != oAuth2Provider) {
                throw EmailDuplicateException()
            }
            val atRtoDto = atRtService.createAtRt(user.id, user.authorities.map { SimpleGrantedAuthority(it.role.name) })
            return OAuth2LoginDto(isRegistered = true, memberId = user.id, accessToken = atRtoDto.accessToken, refreshToken = atRtoDto.refreshToken)
        } else { // 가입되지 않은 사용자인 경우
            val authToken =
                jwtService.createToken(
                    "email" to userProfile.email,
                    "type" to JwtType.OAUTH2,
                    "provider" to oAuth2Provider,
                    expirationSeconds = authProperties.oauth2JwtExpiration,
                )
            return OAuth2LoginDto(isRegistered = false, email = userProfile.email, nickname = userProfile.name, imageUrl = userProfile.imageUrl, authToken = authToken)
        }
    }

    fun logout(
        accessToken: String?,
        refreshToken: String?,
    ) {
        accessToken?.let { accessToken ->
            atRtService.getRefreshToken(accessToken)?.let { atRtService.deleteRefreshToken(it) }
            atRtService.deleteAccessToken(accessToken)
        }
        refreshToken?.let { refreshToken ->
            atRtService.getAccessToken(refreshToken)?.let { atRtService.deleteAccessToken(it) }
            atRtService.deleteRefreshToken(refreshToken)
        }
    }

    @Transactional
    fun resetPassword(
        emailAuthToken: String,
        newPassword: String,
    ) {
        // emailAuthToken 검증
        if (!jwtService.validate(emailAuthToken)) {
            throw InvalidTokenException()
        }
        val emailFromToken = jwtService.getClaim(emailAuthToken, "email", String::class.java) ?: throw InvalidTokenException()

        // 유저 존재 여부 확인
        val member = memberRepository.findByEmail(emailFromToken) ?: throw EmailNotFoundException()

        // 패스워드 유효성 검사
        // 적절한 비밀번호 패턴인지 검사
        if (!validatePassword(newPassword)) {
            throw InvalidPasswordException()
        }
        // 새 비밀번호와 기존 비밀번호가 같은지 검사
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw DuplicateNewPasswordException()
        }

        // 비밀번호 업데이트
        member.updatePassword(passwordEncoder.encode(newPassword))
    }
}
