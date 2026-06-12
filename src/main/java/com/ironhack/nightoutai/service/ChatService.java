package com.ironhack.nightoutai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatClient chatClientWithMemory;
    private final ChatMemory chatMemory;

    public ChatService(ChatModel chatModel, ChatMemory chatMemory) {
        // Usamos ChatModel directamente para tener builders independientes
        this.chatClient = ChatClient.builder(chatModel).build();
        this.chatClientWithMemory = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.chatMemory = chatMemory;
    }

    public String chat(String userMessage) {
        return chatClient.prompt(userMessage)
                .call()
                .content();
    }

    public String ask(String question) {
        return chatClient.prompt(question)
                .call()
                .content();
    }

    public String chatBot(String conversationId, String message) {
        return chatClientWithMemory.prompt(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}