package dev.nikosg.poc.aitoolbox1.ai;

import dev.nikosg.poc.aitoolbox1.awesome.RestTemplateHelper;
import dev.nikosg.poc.aitoolbox1.tooling.ToolExecutor;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolCall;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;
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
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplateHelper restTemplateHelper;
    private final ToolExecutor toolExecutor;

    @Value("${app.openai.apikey}")
    private String apiKey;

    public OpenAiService(RestTemplateHelper restTemplateHelper, ToolExecutor toolExecutor) {
        this.restTemplateHelper = restTemplateHelper;
        this.toolExecutor = toolExecutor;
    }

    public String chat(String userInput) throws Exception {
        List<ToolDef> tools = toolExecutor.getToolSchemas();
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
            toolCalls.forEach(tc-> requestMessages.add(createToolMessage(tc.getId(), executeTool(tc))));

            // Recursive call with updated messages
            return continueChat(requestMessages, tools);
        } else {
            // 🧠 Final response
            String finalAnswer = responseMessage.getContent();
            System.out.println("🧠 Final GPT Response:\n" + finalAnswer);
            return finalAnswer;
        }
    }

    private AiResponse sendToAI(AiRequest request) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.put("Authorization", "Bearer " + apiKey);
        return restTemplateHelper.postForEntity(AiResponse.class, OPENAI_API_URL, headers, request);
    }

    private String executeTool(ToolCall toolCall) {
        String name = toolCall.getFunction().getName();
        String argsJson = toolCall.getFunction().getArguments();
        try {
            return toolExecutor.execute(name, argsJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
