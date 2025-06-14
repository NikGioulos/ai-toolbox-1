package dev.nikosg.poc.aitoolbox1.ai;

public interface OpenAiService {
    String chat(String conversationId, String userPrompt) throws Exception;

}
