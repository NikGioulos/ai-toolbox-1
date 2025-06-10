package dev.nikosg.poc.aitoolbox1.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolCall;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiMessage {
    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;
    @JsonProperty("tool_call_id")
    private String toolCallId;
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    public AiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public AiMessage(String role, List<ToolCall> toolCalls) {
        this.role = role;
        this.toolCalls = toolCalls;
    }

    public AiMessage(String role, String toolCallId, String content) {
        this.role = role;
        this.toolCallId = toolCallId;
        this.content = content;
    }

    public AiMessage() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<ToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }
}
