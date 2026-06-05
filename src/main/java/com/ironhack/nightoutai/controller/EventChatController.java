package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.service.EventChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event-chat")
public class EventChatController {
    private final EventChatService eventChatService;

    public EventChatController(EventChatService eventChatService) {
        this.eventChatService = eventChatService;
    }

    @GetMapping("/recommend")
    public String recommend(@RequestParam String message) {
        return eventChatService.recommend(message);
    }
}
