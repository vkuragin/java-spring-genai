package dev.vk.spring.genai.dto;

import java.util.List;

public record OpenAiSummaryResponse(
        String summary,
        List<String> distributionByLevels
) {
}
