package dev.nikosg.poc.aitoolbox1.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiRequest {
    @JsonProperty("model")
    private final String model = "gpt-4";
    @JsonProperty("messages")
    private final List<AiMessage> messages;
    @JsonProperty("tools")
    private final List<ToolDef> tools;
    @JsonProperty("tool_choice")
    private String toolChoice;

    public AiRequest(List<AiMessage> messages) {
        this(messages, null);
    }

    public AiRequest(List<AiMessage> messages, List<ToolDef> tools) {
        this.messages = messages;
        this.tools = tools;
        if (tools != null && !tools.isEmpty()) {
            this.toolChoice = "auto";
        }
    }

    public List<AiMessage> getMessages() {
        return messages;
    }

    public List<ToolDef> getTools() {
        return tools;
    }
}
