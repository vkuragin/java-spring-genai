package dev.vk.spring.genai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chat/geminiai")
public class GeminiAiChatController {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiChatController.class);

    private static final String SYSTEM_CONTEXT = "You need to generate javadoc for provided java methods " +
            "or return error if the input is not java code. Limit output to 1000 characters";

    @Autowired
    @Qualifier("vertexAiGeminiChatClient")
    private ChatClient chatClient;

    private StopWatch stopWatch = new StopWatch();

    @PostMapping("prompt")
    private ChatClientResponse prompt(@RequestBody String content) {
        stopWatch.start();
        var response = chatClient.prompt()
                .system(SYSTEM_CONTEXT)
                .user(content)
                .call();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        return response.chatClientResponse();
    }
}
