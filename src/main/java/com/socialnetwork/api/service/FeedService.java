package com.socialnetwork.api.service;

import java.lang.reflect.Array;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedService {
    private SimpMessagingTemplate template;

    private FriendService friendService;

    @Autowired
    public FeedService(SimpMessagingTemplate template, FriendService friendService) {

        this.template = template;
        this.friendService = friendService;
    }

    public boolean postFeed(Integer userId, String text) {
        var friends = friendService.getListOfFriendsIds(userId);
        if (friends != null) {
            friends.forEach(friend -> {
                template.convertAndSend("/topic/feed." + String.valueOf(friend),
                    "user " + userId +" posted: " + text);
            });
            return true;
        }
        return false;
    }
}
