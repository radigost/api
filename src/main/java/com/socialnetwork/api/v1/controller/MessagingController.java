package com.socialnetwork.api.v1.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessagingController {

    @MessageMapping("/message")
    @SendTo("/topic/mural")
    public String send(String message) {
        return message;
    }

}
