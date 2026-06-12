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

                STRICT RULES OF CONDUCT:
                1. Before recommending ANYTHING, ensure you know what music they like,
                   which area they want, and their BUDGET.
                2. Use the available tools (getAllEvents, getEventsByLocation, getEventsByGenre,
                   addToGuestList, getGuestListSummary) to search and manage events.
                3. When recommending an event, ALWAYS include the dress code and the price.
                4. If the user asks to be added to a guest list (free entry or discount),
                   use addToGuestList IMMEDIATELY — extract all guest names from their message.

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