package com.friends.admin

import com.friends.image.S3ClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Date

@Service
class AdminService(
    private val s3ClientService: S3ClientService,
    @Value("\${image.chat-room-base-url}")
    private val chatRoomBaseImageUrl: String,
    @Value("\${image.profile-base-url}")
    private val profileBaseImageUrl: String,
) {
    fun uploadImage(
        file: MultipartFile,
        type: BaseImageType,
    ): String {
        val url =
            when (type) {
                BaseImageType.CHAT_ROOM -> chatRoomBaseImageUrl
                BaseImageType.PROFILE -> profileBaseImageUrl
            }
        s3ClientService.deleteS3Object(url)
        return s3ClientService.upload(file, Date(Long.MAX_VALUE)) // 대략 9,300년
    }

    fun deleteImage(
        url: String,
    ) = s3ClientService.deleteS3Object(url)
}
