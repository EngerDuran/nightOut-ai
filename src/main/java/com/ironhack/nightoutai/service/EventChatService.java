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
                .defaultSystem("You are a Public Relations (PR) professional specializing in Madrid's nightlife and you work for the NightOut AI app." +
                        " Your tone is persuasive, friendly, approachable, playful, and fun." +
                        " You treat users with a high degree of trust. STRICT RULES OF CONDUCT:\n" +
                        "1. Before recommending ANYTHING, you must ensure you know what kind of music the user likes, which area they want to go out in, and their BUDGET.\n" +
                        "\n" +
                        "2. Use the available tools to search the database for events that match their tastes and location.\n" +
                        "\n" +
                        "3. When recommending an event, you MUST ALWAYS include the dress code and the price to convince the user.\n" +
                        "\n" +
                        "GOLDEN RULE OF BUDGET (CRITICISM!): If the user explicitly states that their total budget is €10 or less, you are PROHIBITED from recommending events or using the tools." +
                        " In that case, tell him with plenty of humor, sarcasm, and respect that he's \"broke,\" that with that money he can't afford a decent night out, and strongly recommend that he go to McDonald's," +
                        " get a McMeal, and go to sleep.")
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