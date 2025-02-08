package com.friends.image

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.apache.tomcat.util.http.fileupload.FileUploadException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Date

@Service
class S3ClientService(
    private val s3Client: AmazonS3,
) {
    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucketName: String

    @Value("\${cloud.aws.s3.region.static}")
    private lateinit var region: String

    fun upload(
        multipartFile: MultipartFile,
        expirationTime: Date = Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30), // s3 비용 절감을 위해 30일로 설정
    ): String {
        val filename =
            java.util.UUID
                .randomUUID()
                .toString()

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = multipartFile.contentType
        objectMetadata.contentLength = multipartFile.size
        objectMetadata.expirationTime = expirationTime
        try {
            s3Client.putObject(bucketName, filename, multipartFile.inputStream, objectMetadata)
        } catch (e: Exception) {
            throw FileUploadException("S3 파일 업로드에 실패했습니다. ==> ${e.message}")
        }

        return "https://$bucketName.s3.$region.amazonaws.com/$filename"
    }

    fun isExist(fileUrl: String): Boolean {
        val fileKey = fileUrl.substringAfterLast("/")
        return s3Client.doesObjectExist(bucketName, fileKey)
    }

    //프로필 수정 시, 이전 이미지 s3 버킷에서 삭제 로직
    fun deleteS3Object(fileUrl: String) {
        val fileKey = fileUrl.substringAfterLast("/")
        s3Client.deleteObject(bucketName, fileKey)
    }
}
