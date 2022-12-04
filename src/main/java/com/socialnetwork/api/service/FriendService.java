package com.socialnetwork.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Array;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FriendService {
    @Autowired
    @Qualifier("jdbcTemplate2")
    JdbcTemplate jdbcTemplate;

    ObjectMapper mapper = new ObjectMapper();


    @SneakyThrows
    public JsonNode getListOfFriendsIds(int userId) {
        String query = "SELECT friends FROM socialnetwork.friend WHERE id=?";
        var listString =  jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                rs.getString("friends"),
            userId);
        var res  = mapper.readTree(listString.get(0));
        return res;
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
