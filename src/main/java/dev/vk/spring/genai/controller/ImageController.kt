package dev.vk.spring.genai.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClientResponse
import org.springframework.ai.image.Image
import org.springframework.ai.image.ImageGeneration
import org.springframework.ai.image.ImageModel
import org.springframework.ai.image.ImageOptions
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.openai.OpenAiImageOptions
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("chat/images")
class ImageController(@Qualifier("openAiChatClient") val chatClient: ChatClient,
    val imageModel: ImageModel) {

    private val log: Logger = LoggerFactory.getLogger(ImageController::class.java)

    private companion object {
        const val SYSTEM_PROMPT_ANALYZE: String = "Analyze picture and prepare summary: " +
                "1. what is there depicted " +
                "2. What is the idea behind it " +
                "3. If any text display it as is"

        const val SYSTEM_PROMPT_GENERATE: String = "You are a designer. You read the input containing job description:" +
                "theme, name, text. Using these parameters you need to generate image according to the theme mentioned, " +
                "with company's or person's name written on it, " +
                "and optional text (text should not exceed 100 chars). "
    }


    @RequestMapping(
        "/upload-and-analyze",
        method = [RequestMethod.POST],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadAndAnalyzeImage(@RequestPart("file") file: MultipartFile): ChatClientResponse {
        log.info("Image: {} ({} bytes), type: {}", file.name, file.size, file.contentType)
        return chatClient.prompt()
            .system(SYSTEM_PROMPT_ANALYZE)
            .user {
                it.text("Picture: " + file.name)
                it.media(MimeType.valueOf("image/*"), file.resource)
            }
            .call()
            .chatClientResponse()
    }

    @RequestMapping("/generate-image")
    fun generateImage(@RequestParam("job-desc") jobDesc: String): Image? {
        log.info("Generating image according to description: {}", jobDesc)
        val imageOptions = OpenAiImageOptions.builder()
            .width(1024)
            .height(1024)
            .n(1)
            .build()
        val imagePrompt = ImagePrompt("$SYSTEM_PROMPT_GENERATE Job description: $jobDesc", imageOptions)
        return imageModel.call(imagePrompt)
            .result?.output
    }
}