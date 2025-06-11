package dev.nikosg.poc.aitoolbox1.ai;

import dev.nikosg.poc.aitoolbox1.awesome.RestTemplateHelper;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolCall;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistry;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistryProvider;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.nikosg.poc.aitoolbox1.ai.AiMessage.*;

@Service
public class OpenAiService {
    private final RestTemplateHelper restTemplateHelper;
    private final ToolRegistry toolRegistry;

    @Value("${app.openai.chat.apikey}")
    private String apiKey;

    @Value("${app.openai.chat.url}")
    private String url;

    public OpenAiService(RestTemplateHelper restTemplateHelper, ToolRegistryProvider toolRegistryProvider) {
        this.restTemplateHelper = restTemplateHelper;
        this.toolRegistry = toolRegistryProvider.provide(ToolRegistryType.METHOD);
    }

    public String chat(String userInput) throws Exception {
        List<ToolDef> tools = toolRegistry.getToolSchemas();

        List<AiMessage> messages = new ArrayList<>();
        messages.add(createUserMessage(userInput));
        return continueChat(messages, tools);
    }

    private String continueChat(List<AiMessage> requestMessages, List<ToolDef> tools) throws Exception {
        AiRequest request = new AiRequest(requestMessages, tools);

        AiResponse response = sendToAI(request);
        AiMessage responseMessage = response.getChoices().stream().map(Choice::getMessage).findFirst().orElseThrow();
        List<ToolCall> toolCalls = responseMessage.getToolCalls();
        if (toolCalls != null && !toolCalls.isEmpty()) {
            // Add assistant's message
            requestMessages.add(createAssistantMessage(toolCalls));

            // call each tool
            toolCalls.forEach(tc -> requestMessages.add(createToolMessage(tc.getId(), executeTool(tc))));

            // Recursive call with updated messages
            return continueChat(requestMessages, tools);
        } else {
            // Final response
            String finalAnswer = responseMessage.getContent();
            System.out.println("🧠 Final AI Response:\n" + finalAnswer);
            return finalAnswer;
        }
    }

    private AiResponse sendToAI(AiRequest request) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.put("Authorization", "Bearer " + apiKey);
        return restTemplateHelper.postForEntity(AiResponse.class, url, headers, request);
    }

    private String executeTool(ToolCall toolCall) {
        String name = toolCall.getFunction().getName();
        String argsJson = toolCall.getFunction().getArguments();
        try {
            return toolRegistry.execute(name, argsJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
