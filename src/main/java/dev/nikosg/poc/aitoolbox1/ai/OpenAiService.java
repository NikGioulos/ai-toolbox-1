package dev.nikosg.poc.aitoolbox1.ai;

import dev.nikosg.poc.aitoolbox1.awesome.RestTemplateHelper;
import dev.nikosg.poc.aitoolbox1.tooling.ToolExecutor;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolCall;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplateHelper restTemplateHelper;
    private final ToolExecutor toolExecutor;

    @Value("${app.openai.apikey}")
    private String apiKey;

    public OpenAiService(RestTemplateHelper restTemplateHelper, ToolExecutor toolExecutor) {
        this.restTemplateHelper = restTemplateHelper;
        this.toolExecutor = toolExecutor;
    }

    public void chat(String userInput) throws Exception {
        List<ToolDef> tools = toolExecutor.getToolSchemas();

        // Initial request
        AiMessage message = new AiMessage("user", userInput);
        AiRequest request = new AiRequest(List.of(message), tools);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.put("Authorization", "Bearer " + apiKey);

        AiResponse response = restTemplateHelper.postForEntity(AiResponse.class, OPENAI_API_URL, headers, request);
        AiMessage choiceMessage = response.getChoices().stream().map(Choice::getMessage).findFirst().orElseThrow();
        List<ToolCall> toolCalls = choiceMessage.getToolCalls();
        if (toolCalls != null && !toolCalls.isEmpty()) {
            for (ToolCall toolCall : toolCalls) {
                String id = toolCall.getId();
                String name = toolCall.getFunction().getName();
                String argsJson = toolCall.getFunction().getArguments();

                String toolOutput = toolExecutor.execute(name, argsJson);

                // Build follow-up messages
                List<AiMessage> messages = List.of(
                        new AiMessage("user", userInput),
                        new AiMessage("assistant", List.of(toolCall)),
                        new AiMessage("tool", id, toolOutput)
                );

                AiRequest secondRequest = new AiRequest(messages, tools);
                AiResponse finalResponse = restTemplateHelper.postForEntity(AiResponse.class, OPENAI_API_URL, headers, secondRequest);

                System.out.println("🧠 Response:\n" + finalResponse.getChoices().get(0).getMessage().getContent());
            }
        } else {
            // If no tool_call, just print normal response
            System.out.println("🧠 GPT Response:\n" + choiceMessage.getContent());
        }
    }
}
