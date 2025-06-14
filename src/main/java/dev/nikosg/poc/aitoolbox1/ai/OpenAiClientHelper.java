package dev.nikosg.poc.aitoolbox1.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAiClientHelper {
    private final OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    ChatCompletionMessage sendToAi(List<ChatCompletionMessageParam> messages, List<ChatCompletionTool> tools) {
        ChatCompletionCreateParams request = buildRequest(messages, tools);
        ChatCompletion response = send(request);
        return extractResponseMessage(response);
    }

    private ChatCompletionCreateParams buildRequest(List<ChatCompletionMessageParam> messages, List<ChatCompletionTool> tools) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .messages(messages)
                .model(ChatModel.GPT_4);
        if (tools != null) {
            builder.tools(tools);
            builder.toolChoice(ChatCompletionToolChoiceOption.Auto.AUTO);
        }
        return builder.build();
    }

    private ChatCompletion send(ChatCompletionCreateParams params) {
        return client.chat().completions().create(params);
    }

    private ChatCompletionMessage extractResponseMessage(ChatCompletion chatCompletion) {
        return chatCompletion.choices().stream().map(ChatCompletion.Choice::message).findFirst().orElseThrow();
    }
}
