package com.friends.email

import jakarta.mail.Message
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    private val executorService: ExecutorService,
) {
    fun sendHtml(
        to: String,
        subject: String,
        html: String,
    ) {
        executorService.submit {
            try {
                val message = javaMailSender.createMimeMessage()
                message.subject = subject
                message.setText(html, "utf-8", "html")
                message.setRecipients(
                    Message.RecipientType.TO,
                    to,
                )
                javaMailSender.send(message)
            } catch (e: Exception) {
                throw EmailSendFailedException()
            }
        }
    }
}
