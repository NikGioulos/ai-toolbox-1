package dev.nikosg.poc.aitoolbox1.ai;

import com.openai.models.chat.completions.*;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistry;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistryProvider;
import dev.nikosg.poc.aitoolbox1.tooling.registry.ToolRegistryType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class OpenAiServiceImpl implements OpenAiService {
    private final ToolRegistry toolRegistry;
    private final ConversationService conversationService;
    private final OpenAiClientHelper openAiClientHelper;

    private List<ChatCompletionTool> tools;

    public OpenAiServiceImpl(ToolRegistryProvider toolRegistryProvider, ConversationService conversationService, OpenAiClientHelper openAiClientHelper) {
        this.toolRegistry = toolRegistryProvider.provide(ToolRegistryType.METHOD);
        this.conversationService = conversationService;
        this.openAiClientHelper = openAiClientHelper;
    }

    @Override
    public String chat(String conversationId, String userPrompt) throws Exception {
        fetchTools();
        conversationService.addMessage(conversationId, buildUserMessageParam(userPrompt));
        return continueChat(conversationId);
    }

    private String continueChat(String conversationId) {
        ChatCompletionMessage responseMessage = sendToAI(conversationId);
        if (responseMessage.toolCalls().isPresent()) {
            List<ChatCompletionMessageToolCall> toolCalls = responseMessage.toolCalls().get();
            // Add assistant's message
            conversationService.addMessage(conversationId, buildAssistantMessageParam(toolCalls));

            // call each tool
            toolCalls.forEach(tc -> {
                String toolResponse = executeTool(tc);
                conversationService.addMessage(conversationId, buildToolMessageParam(tc.id(), toolResponse));
            });

            // Recursive call no remaining tool to be called
            return continueChat(conversationId);
        } else {
            // Final response
            String finalAnswer = responseMessage.content().orElseThrow();
            System.out.println("🧠 Final AI Response:\n" + finalAnswer);
            return finalAnswer;
        }
    }

    private ChatCompletionMessage sendToAI(String conversationId) {
        List<ChatCompletionMessageParam> conversation = conversationService.getMessages(conversationId);
        return openAiClientHelper.sendToAi(conversation, tools);
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

    private void fetchTools() {
        try {
            if(tools == null) {
                tools = toolRegistry.getToolSchemas();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
