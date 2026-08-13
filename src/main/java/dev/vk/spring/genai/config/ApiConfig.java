package dev.vk.spring.genai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel model, SimpleLoggerAdvisor loggerAdvisor) {
        return ChatClient.builder(model)
                .defaultAdvisors(loggerAdvisor)
                .build();
    }

    @Bean
    public ChatClient vertexAiGeminiChatClient(GoogleGenAiChatModel model, SimpleLoggerAdvisor loggerAdvisor) {
        return ChatClient.builder(model)
                .defaultAdvisors(loggerAdvisor)
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel model, SimpleLoggerAdvisor loggerAdvisor) {
        return ChatClient.builder(model)
                .defaultAdvisors(loggerAdvisor)
                .build();
    }

    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }
}
