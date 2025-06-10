package dev.nikosg.poc.aitoolbox1.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Choice {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("message")
    private AiMessage message;
    @JsonProperty("finish_reason")
    private String finishReason;

    public Integer getId() {
        return id;
    }

    public AiMessage getMessage() {
        return message;
    }

    public String getFinishReason() {
        return finishReason;
    }
}
