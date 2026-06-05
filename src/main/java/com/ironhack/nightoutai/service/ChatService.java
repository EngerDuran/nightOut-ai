package com.ironhack.nightoutai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatClient chatClientWithMemory;
    private final ChatMemory chatMemory;

    public ChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder.build();
        this.chatClientWithMemory = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .build()).build();
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