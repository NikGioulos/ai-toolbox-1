package dev.nikosg.poc.aitoolbox1.tooling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ToolCall {
    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("function")
    private ToolFunctionCall function;

    public ToolCall(String id, ToolFunctionCall function) {
        this.id = id;
        this.function = function;
        this.type = "function";
    }


    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public ToolFunctionCall getFunction() {
        return function;
    }
}
