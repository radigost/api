package com.socialnetwork.api.v1.controller;

import com.socialnetwork.api.service.FeedService;
import com.socialnetwork.api.v1.domain.PaginatedListResponseDto;
import com.socialnetwork.api.v1.domain.PostFeedDto;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/api/profile/{profileId}/feed")
public class FeedController {
    private FeedService feedService;


    @PostMapping("")
    public String postToFeed(
        @PathVariable int profileId,
        @RequestBody String text
    ) {
        var data = PostFeedDto.builder().profileId(profileId).text(text).build();
        feedService.addPostToFeed(data);
        return text;
    }

    @GetMapping("")
    public PaginatedListResponseDto getFeed(
        @PathVariable int profileId,
        @RequestParam(required = false) Integer pageOffset,
        @RequestParam(required = false) Integer pageSize
    ) {
        Integer DEFAULT_PAGESIZE = 20;
        Integer DEFAULT_OFFSET = 0;

        return feedService.getFeed(
            profileId,
            Optional.ofNullable(pageOffset).orElse(DEFAULT_OFFSET),
            Optional.ofNullable(pageSize).orElse(DEFAULT_PAGESIZE)
        );
    }

}
