package com.socialnetwork.api.v1.controller;

import com.socialnetwork.api.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/api/profile/{id}/feed")
public class FeedController {
    private FeedService feedService;

    @PostMapping("")
    public String postToFeed(
        @PathVariable int id,
        @RequestBody String text
    ) {
        feedService.postFeed(id, text);
        return text;
    }

}
