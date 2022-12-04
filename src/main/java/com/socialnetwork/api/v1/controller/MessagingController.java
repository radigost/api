package com.socialnetwork.api.v1.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class MessagingController {

    @MessageMapping("/message")
    @SendTo("/topic/mural")
    public String send(String message) throws Exception {
        return message;
    }

}
