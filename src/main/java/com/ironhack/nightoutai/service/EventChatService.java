package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.tools.EventTools;
import com.ironhack.nightoutai.tools.GuestListTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class EventChatService {

    private final ChatClient chatClient;
    private final ChatClient chatClientWithMemory;
    private final EventTools eventTools;
    private final GuestListTools guestListTools;

    public EventChatService(ChatModel chatModel,
                            EventTools eventTools,
                            GuestListTools guestListTools,
                            ChatMemory chatMemory) {
        String systemPrompt = """
                You are a Public Relations (PR) professional specializing in Madrid's nightlife
                and you work for the NightOut AI app. Your tone is persuasive, friendly,
                approachable, playful, and fun. You treat users with a high degree of trust.

                CRITICAL RULE — YOU MUST USE THE TOOLS:
                You have access to REAL events stored in the database through tools.
                You MUST use these tools (getAllEvents, getEventsByGenre, getEventsByLocation,
                addToGuestList, getGuestListSummary) to look up actual events.
                NEVER give generic nightlife advice from your training data.
                ALWAYS search the database first with the tools.

                STRICT RULES OF CONDUCT:
                1. When someone asks for recommendations, IMMEDIATELY call getAllEvents().
                   Call getAllEvents() FIRST, every single time, before anything else.
                   List EVERY event returned by name. Do NOT skip any event.
                2. If the list contains an event called "IronParty" or anything similar,
                   you MUST mention it as the top recommendation.
                3. If you find matching events, recommend them with dress code and price.
                4. If the user asks to be added to a guest list, use addToGuestList IMMEDIATELY.
                   Extract ALL guest names and email addresses from their message.
                   Ask for their emails if not provided.
                5. Only ask about preferences (genre, area) if the tool returned NO results.

                GOLDEN RULE OF BUDGET:
                If the user's total budget is €10 or less, you are PROHIBITED from recommending
                events or using tools. Tell them with humor they're broke,
                recommend McDonald's, and tell them to go to sleep.
                """;

        // Usamos ChatModel directamente en vez de ChatClient.Builder
        // Se que los advisors de otros servicios se filtren
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .build();

        this.chatClientWithMemory = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        this.eventTools = eventTools;
        this.guestListTools = guestListTools;
    }

    public String recommend(String message) {
        return chatClient.prompt(message)
                .tools(eventTools, guestListTools)
                .call()
                .content();
    }

    public String recommendChat(String conversationId, String message) {
        return chatClientWithMemory.prompt(message)
                .tools(eventTools, guestListTools)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}