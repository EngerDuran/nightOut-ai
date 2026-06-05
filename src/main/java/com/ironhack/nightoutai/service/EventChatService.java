package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.tools.EventTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class EventChatService {

    // Guardamos el Cliente construido
    private final ChatClient chatClient;
    private final EventTools eventTools;

    public EventChatService(ChatClient.Builder chatClientBuilder, EventTools eventTools) {
        //Construimos el cliente cuando arrancamos spring
        this.chatClient = chatClientBuilder
                .defaultSystem("You are a NightOut AI assistant. You ONLY recommend events" +
                        " that you obtain by running your tools. UNDER NO CIRCUMSTANCES should" +
                        " you invent plans, locations, or recommend external places" +
                        " that are not in your database. DATABASE ONLY!")
                .build();

        this.eventTools = eventTools;
    }

    public String recommend(String message) {
        // Usamos el cliente existente para responder
        return chatClient.prompt(message)
                .tools(eventTools)
                .call()
                .content();
    }
}