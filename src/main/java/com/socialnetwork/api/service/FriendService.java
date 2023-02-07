package com.socialnetwork.api.service;

import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FriendService {
    @Autowired
    @Qualifier("jdbcTemplateMain")
    JdbcTemplate jdbcTemplate;

    @SneakyThrows
    public List<Integer> getListOfFollowers(int profileId) {
        String query = "SELECT * FROM socialnetwork.followers WHERE followee_id=?";
        var listString = jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                rs.getInt("follower_id"),
            profileId);

        return !listString.isEmpty() ? listString : List.of();
    }

    @SneakyThrows
    public boolean addFriend(int profileId, List<Integer> friendIds) {
        log.info("adding friends {} and {}", profileId, friendIds);

        friendIds.forEach(followeeId -> {
            jdbcTemplate.update(
                "INSERT INTO socialnetwork.followers (follower_id, followee_id) VALUES (?,?)",
                profileId,
                followeeId
            );
        });
        return true;
    }
}
