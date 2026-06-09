package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return chatService.ask(question);
    }

    @GetMapping("/chatbot/{conversationId}")
    public String chatBot(@PathVariable String conversationId, @RequestParam String message) {
        return chatService.chatBot(conversationId, message);
    }
}