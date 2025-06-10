package dev.nikosg.poc.aitoolbox1.tooling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ToolDef {
    @JsonProperty("type")
    private final String type = "function";
    @JsonProperty("function")
    private final ToolFunctionDef function;

    public ToolDef(ToolFunctionDef function) {
        this.function = function;
    }
}
