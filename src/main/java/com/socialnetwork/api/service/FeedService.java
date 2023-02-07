package com.socialnetwork.api.service;

import com.socialnetwork.api.v1.domain.PaginatedListResponseDto;
import com.socialnetwork.api.v1.domain.PostFeedDto;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedService {
    private SimpMessagingTemplate liveUpdateSender;

    private FriendService friendService;

    private RedissonClient memoryCache;

    private final JdbcTemplate jdbcTemplateMain;

    private final Integer CACHE_SIZE = 1000;

    private final Integer CACHED_TIMELINE_EXPIRATION_DAYS = 30;


    @Autowired
    public FeedService(
        SimpMessagingTemplate template,
        FriendService friendService,
        RedissonClient memoryCache,
        @Qualifier("jdbcTemplateMain") JdbcTemplate jdbcTemplateMain
    ) {
        this.liveUpdateSender = template;
        this.friendService = friendService;
        this.memoryCache = memoryCache;
        this.jdbcTemplateMain = jdbcTemplateMain;
    }

    public boolean addPostToFeed(PostFeedDto post) {
        var friends = friendService.getListOfFollowers(post.getProfileId());
        if (friends != null) {
            friends.forEach(friendId -> {
                addPostToSocket(post, friendId.toString());
                addPostToCache(post, friendId.toString());
            });
            addPostToDb(post);
            return true;
        }
        return false;
    }

    public PaginatedListResponseDto getFeed(Integer profileId, Integer pageOffset, Integer pageSize) {
        List<PostFeedDto> posts;

        var feedCache = getCachedTimeline(profileId);
        if (feedCache.isExists() && CACHE_SIZE > pageOffset + pageSize) {
            posts = feedCache.range(
                pageOffset,
                pageSize
            );
        } else {
            posts = getFriendPostsFromDb(profileId, pageOffset, pageSize);
            // TODO make rebuild async
            rebuildCachedTimeline(profileId);
        }

        return PaginatedListResponseDto.builder().list(posts).build();
    }

    private void addPostToSocket(PostFeedDto data, String friend) {
        liveUpdateSender.convertAndSend("/topic/feed." + friend,
            "user " + data.getProfileId() + " posted: " + data.getText());
    }

    private boolean addPostToCache(PostFeedDto post, String profileId) {
        var key = "timeline:" + profileId;
        var cachedTimeline = memoryCache.getDeque(key);
        if (cachedTimeline.isExists()) {
            var size = cachedTimeline.size();
            if (size >= CACHE_SIZE) {
                cachedTimeline.removeLast();
            }
            cachedTimeline.addFirst(post);
        } else {
            cachedTimeline.addFirst(post);
            cachedTimeline.expire(Duration.ofDays(CACHED_TIMELINE_EXPIRATION_DAYS));
        }
        return true;
    }

    private boolean addPostToDb(PostFeedDto data) {
        log.info("adding post for user {}", data.getProfileId());
        var date = Instant.now();
        var profileId = data.getProfileId();
        var textToInsert = data.getText();
        var result = jdbcTemplateMain.update(
            "INSERT INTO socialnetwork.post (profile_id,text,creation_date) VALUES (?,?,?)",
            profileId,
            textToInsert,
            date
        );
        return result == 1;
    }

    private RList<PostFeedDto> getCachedTimeline(Integer profileId) {
        var key = "timeline:" + profileId;
        return memoryCache.getList(key);
    }

    private void rebuildCachedTimeline(Integer profileId) {
        var feedCache = getCachedTimeline(profileId);
        feedCache.clear();
        var postsToCache = getFriendPostsFromDb(profileId, 0, CACHE_SIZE);
        postsToCache.forEach(post -> addPostToCache(post, profileId.toString()));
    }



    private List<PostFeedDto> getFriendPostsFromDb(Integer profileId, Integer pageOffset, Integer pageSize) {
        var friends = friendService.getListOfFollowers(profileId);

        ArrayList<Integer> arguments = new ArrayList<>();
        arguments.addAll(friends);

        String inSql = String.join(",", Collections.nCopies(arguments.size(), "?"));

        arguments.add(pageSize);
        arguments.add(pageOffset);

        return jdbcTemplateMain.query(
            String.format(
                "SELECT * from socialnetwork.post WHERE profile_id IN (%s) ORDER BY creation_date DESC LIMIT ? OFFSET ?",
                inSql),
            arguments.toArray(),
            (rs, rowNum) -> PostFeedDto.builder().
                text(rs.getString("text"))
                .profileId(rs.getInt("profile_id"))
                .creationDate(rs.getTimestamp("creation_date").toLocalDateTime()).build()
        );
    }
}
