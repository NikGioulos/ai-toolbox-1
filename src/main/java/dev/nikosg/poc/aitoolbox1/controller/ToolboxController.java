package dev.nikosg.poc.aitoolbox1.controller;

import dev.nikosg.poc.aitoolbox1.ai.OpenAiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
class ToolboxController {
    private final OpenAiService chatService;

    private final List<ChatMessage> history = new ArrayList<>();

    ToolboxController(OpenAiService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ui/{user}")
    public String chatPage(@PathVariable String user, Model model) {
        model.addAttribute("history", history);
        model.addAttribute("message", new ChatMessage(user, ""));
        return "chat";
    }

    @PostMapping("/send")
    public String sendPrompt(@ModelAttribute ChatMessage message) throws Exception {
        history.add(new ChatMessage(message.sender(), message.content()));

        String aiResponse = chatService.chat(message.sender(), message.content());
        history.add(new ChatMessage("ai", aiResponse));

        return "redirect:/ui/" + message.sender();
    }

    @GetMapping("/{user}/chat")
    @ResponseBody
    String chat(@PathVariable String user, @RequestParam String prompt) throws Exception {
        return chatService.chat(user, prompt);
    }
}
