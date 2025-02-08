package com.friends.image.service

import com.friends.image.InvalidImageUrlException
import com.friends.image.S3ClientService
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageCommandService(
    private val memberRepository: MemberRepository,
    private val s3ClientService: S3ClientService,
) {
    fun uploadImage(
        memberId: Long,
        image: MultipartFile,
    ): String {
        memberRepository.existsById(memberId).also { if (!it) throw MemberNotFoundException() }
        return s3ClientService.upload(image)
    }

    fun existsImage(
        originalImageUrl: String?,
        fileUrl: String?,
    ) {
        if (fileUrl != null && originalImageUrl != fileUrl) {
            if (!s3ClientService.isExist(fileUrl)) throw InvalidImageUrlException()
        }
    }

    fun deleteOriginalImage(
        originalImageUrl: String?,
        fileUrl: String?,
    ) {
        if (originalImageUrl != null && originalImageUrl != fileUrl) {
            s3ClientService.deleteS3Object(originalImageUrl)
        }
    }
}
