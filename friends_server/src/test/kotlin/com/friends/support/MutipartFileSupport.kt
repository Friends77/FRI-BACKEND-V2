package com.friends.support

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

const val TEST_IMAGE_FILE_NAME = "testImage"
const val TEST_IMAGE_UPLOAD_FILE_URL = "https://test.com/testImage"

enum class ImageFormat(
    val values: List<String>,
) {
    PNG(listOf("png")),
    JPEG(listOf("jpeg", "jpg")),
    WEBP(listOf("webp")),
    GIF(listOf("gif")),
}

fun createTestImageFile(
    format: ImageFormat = ImageFormat.WEBP,
    name: String = TEST_IMAGE_FILE_NAME,
): MockMultipartFile = createMultipartFile(name, generateImageBytes(format.values.first()).inputStream(), "image/${format.values.first()}")

fun createMultipartFile(
    name: String,
    contentStream: InputStream,
    contentType: String = MediaType.APPLICATION_JSON_VALUE,
): MockMultipartFile = MockMultipartFile(name, name, contentType, contentStream)

fun generateImageBytes(format: String): ByteArray {
    val bufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB) // 1x1 픽셀의 이미지 생성

    ByteArrayOutputStream().use {
        ImageIO.write(bufferedImage, format, it)
        return it.toByteArray()
    }
}
