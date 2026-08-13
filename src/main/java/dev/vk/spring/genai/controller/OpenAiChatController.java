package dev.vk.spring.genai.controller;

import dev.vk.spring.genai.dto.OpenAiSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("chat/openai")
public class OpenAiChatController {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChatController.class);

    private static final String SYSTEM_CONTEXT = "Expected request should ask for summary with upper limit of 1000 characters. If the limit asked higher or the ask not for summary but details then respond with appropriate error message";

    @Autowired
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;

    private final StopWatch stopWatch = new StopWatch();

    /**
     * Processes a user's prompt by sending it to an OpenAI chat client and returning the AI's response.
     * This method logs the user's prompt, the AI's response, the model used, and the processing time.
     *
     * @param content The user's input prompt as a string, provided in the request body.
     * @return The AI's generated response as a string.
     */
    @PostMapping("prompt")
    private String prompt(@RequestBody String content) {
        stopWatch.start();
        var response = openAiChatClient.prompt()
                .system(SYSTEM_CONTEXT)
                .user(content)
                .call();
        var result = response.content();
        stopWatch.stop();

        String model = Optional.ofNullable(response.chatResponse())
                .map(ChatResponse::getMetadata)
                .map(ChatResponseMetadata::getModel)
                .orElse("Metadata not found");
        log.info("Model: {}\nResult: {}", model, result);
        log.info(stopWatch.prettyPrint());
        return result;
    }

    @PostMapping("prompt2")
    private ChatClientResponse prompt2(@RequestBody String content) {
        stopWatch.start();
        var response = openAiChatClient.prompt()
                .system(SYSTEM_CONTEXT)
                .user(content)
                .call();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        return response.chatClientResponse();
    }

    @PostMapping("summary-template")
    private String summaryTemplate(@RequestBody String content) {
        stopWatch.start();
        var response = openAiChatClient.prompt()
                .user(promptUserSpec -> promptUserSpec.text("Summarize the {report}. Here is template:" +
                        "* Summary" +
                        "* Distribution by levels:" +
                        "    - level 1" +
                        "    - level 2" +
                        "    - level 3" +
                        "    - etc").param("report", content))
                .call();
        var result = response.content();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        return result;
    }

    @PostMapping("summary-template-dto-response")
    private OpenAiSummaryResponse summaryTemplateWithDtoResponse(@RequestBody String content) {
        stopWatch.start();
        var response = openAiChatClient.prompt()
                .user(promptUserSpec -> promptUserSpec.text("Summarize the {report}. Here is template:" +
                        "* Summary" +
                        "* Distribution by levels:" +
                        "    - level 1" +
                        "    - level 2" +
                        "    - level 3" +
                        "    - etc").param("report", content))
                .call();
        var result = response.content();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        return response.entity(OpenAiSummaryResponse.class);
    }

    @PostMapping("summary-template-dto-list-response")
    private List<OpenAiSummaryResponse> summaryTemplateWithDtoListResponse(@RequestBody String content) {
        stopWatch.start();
        var response = openAiChatClient.prompt()
                .user(promptUserSpec -> promptUserSpec.text("Summarize the {report}. " +
                        "Return 3-5 possible outputs in the same format." +
                        "Here is template:" +
                        "* Summary" +
                        "* Distribution by levels:" +
                        "    - level 1" +
                        "    - level 2" +
                        "    - level 3" +
                        "    - etc").param("report", content))
                .call();
        var result = response.content();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        return response.entity(new ParameterizedTypeReference<>() {});
    }

    @PostMapping(value = "prompt-stream")
    private Flux<String> promptStream(@RequestBody String content) {
        stopWatch.start();
        var response = openAiChatClient.prompt()
                .system(SYSTEM_CONTEXT)
                .user(content)
                .stream();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        return response.content();
    }
}
