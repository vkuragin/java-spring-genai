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
@RequestMapping("chat/ollama")
public class OllamaChatController {

    private static final Logger log = LoggerFactory.getLogger(OllamaChatController.class);

    private static final String SYSTEM_CONTEXT = "You need to generate email based on user's prompt. Use casual language. " +
            "Limit output to 1000 characters";

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient chatClient;

    private StopWatch stopWatch = new StopWatch();

    @PostMapping("prompt")
    private ChatClientResponse prompt(@RequestBody String content) {
        log.info("User's prompt: {}", content);

        stopWatch.start();
        var response = chatClient.prompt()
                .system(SYSTEM_CONTEXT)
                .user(content)
                .call();
        stopWatch.stop();

        log.info("Result: {}", response.chatClientResponse());
        log.info(stopWatch.prettyPrint());
        return response.chatClientResponse();
    }
}
