package dev.vk.spring.genai.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClientResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("chat/images")
class ImageController(@Qualifier("openAiChatClient") private val chatClient: ChatClient) {

    val log: Logger = LoggerFactory.getLogger(ImageController::class.java)
    private val SYSTEM_PROMPT: String = "Analyze picture and prepare summary: " +
            "1. what is there depicted " +
            "2. What is the idea behind it " +
            "3. If any text display it as is"

    @RequestMapping(
        "/upload-and-analyze",
        method = [RequestMethod.POST],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadAndAnalyzeImage(@RequestPart("file") file: MultipartFile): ChatClientResponse {
        log.info("Image: {} ({} bytes), type: {}", file.name, file.size, file.contentType)
        return chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user {
                it.text("Picture: " + file.name)
                it.media(MimeType.valueOf("image/*"), file.resource)
            }
            .call()
            .chatClientResponse()
    }
}