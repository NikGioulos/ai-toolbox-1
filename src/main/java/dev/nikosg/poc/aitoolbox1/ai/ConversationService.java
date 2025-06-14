package dev.nikosg.poc.aitoolbox1.ai;

import com.openai.models.chat.completions.ChatCompletionMessageParam;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversationService {

    private static final int MAX_MESSAGES = 10;

    private final Map<String, Deque<ChatCompletionMessageParam>> userConversations = new HashMap<>();

    public synchronized void addMessage(String conversationId, ChatCompletionMessageParam message) {
        userConversations.putIfAbsent(conversationId, new LinkedList<>());
        Deque<ChatCompletionMessageParam> messages = userConversations.get(conversationId);

        if (messages.size() >= MAX_MESSAGES) {
            messages.pollFirst(); // remove oldest
        }

        messages.addLast(message);
    }

    public synchronized List<ChatCompletionMessageParam> getMessages(String conversationId) {
        return new ArrayList<>(userConversations.getOrDefault(conversationId, new LinkedList<>()));
    }

    public synchronized void clear(String conversationId) {
        userConversations.remove(conversationId);
    }
}
