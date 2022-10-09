package com.socialnetwork.api.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FriendService {
    @Autowired
    JdbcTemplate jdbcTemplate;


    public String getListOfFriendsIds(int userId) {
        String query = "SELECT friends FROM socialnetwork.friend WHERE id=?";
        return jdbcTemplate.queryForObject(
            query,
            (rs, rowNum) ->
                rs.getString("friends"),
            userId);
    }

    public boolean addFriend(int userId, List<Integer> friendIds) {
        log.info("adding friends {} and {}", userId, friendIds);
        var friendsjson = new JSONArray(friendIds).toString();

        var result = jdbcTemplate.update(
            "INSERT INTO socialnetwork.friend (id, friends) VALUES (?,?)",
            userId,
            friendsjson
        );
        return result == 1;
    }
}
