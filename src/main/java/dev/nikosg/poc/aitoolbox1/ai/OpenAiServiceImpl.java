package dev.nikosg.poc.aitoolbox1.ai;

import com.openai.models.chat.completions.*;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistry;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistryProvider;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistryType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiServiceImpl implements OpenAiService {
    private final ToolRegistry toolRegistry;
    private final OpenAiClientHelper openAiClientHelper;

    public OpenAiServiceImpl(ToolRegistryProvider toolRegistryProvider, OpenAiClientHelper openAiClientHelper) {
        this.toolRegistry = toolRegistryProvider.provide(ToolRegistryType.METHOD);
        this.openAiClientHelper = openAiClientHelper;
    }

    @Override
    public String chat(String userInput) throws Exception {
        List<ChatCompletionTool> tools = toolRegistry.getToolSchemas();
        List<ChatCompletionMessageParam> messages = new ArrayList<>();
        messages.add(buildUserMessageParam(userInput));

        return continueChat(messages, tools);
    }

    private String continueChat(List<ChatCompletionMessageParam> requestMessages, List<ChatCompletionTool> tools) {
        ChatCompletionMessage responseMessage = sendToAI(requestMessages, tools);
        if (responseMessage.toolCalls().isPresent()) {
            List<ChatCompletionMessageToolCall> toolCalls = responseMessage.toolCalls().get();
            // Add assistant's message
            requestMessages.add(buildAssistantMessageParam(toolCalls));

            // call each tool
            toolCalls.forEach(tc -> {
                String tooResponse = executeTool(tc);
                requestMessages.add(buildToolMessageParam(tc.id(), tooResponse));
            });

            // Recursive call with updated messages
            return continueChat(requestMessages, tools);
        } else {
            // Final response
            String finalAnswer = responseMessage.content().orElseThrow();
            System.out.println("🧠 Final AI Response:\n" + finalAnswer);
            return finalAnswer;
        }
    }

    private ChatCompletionMessage sendToAI(List<ChatCompletionMessageParam> messages, List<ChatCompletionTool> tools) {
        return openAiClientHelper.sendToAi(messages, tools);
    }

    private ChatCompletionMessageParam buildUserMessageParam(String content) {
        return ChatCompletionMessageParam.ofUser(ChatCompletionUserMessageParam.builder().content(content).build());
    }

    private ChatCompletionMessageParam buildAssistantMessageParam(List<ChatCompletionMessageToolCall> toolCalls) {
        return ChatCompletionMessageParam.ofAssistant(
                ChatCompletionAssistantMessageParam.builder()
                        .toolCalls(toolCalls)
                        .build());
    }

    private ChatCompletionMessageParam buildToolMessageParam(String toolCallId, String content) {
        return ChatCompletionMessageParam.ofTool(
                ChatCompletionToolMessageParam.builder()
                        .content(content)
                        .toolCallId(toolCallId)
                        .build()
        );
    }

    private String executeTool(ChatCompletionMessageToolCall toolCall) {
        String name = toolCall.function().name();
        String argsJson = toolCall.function().arguments();
        try {
            return toolRegistry.execute(name, argsJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
