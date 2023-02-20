package com.socialnetwork.api.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CounterService {
    @Autowired
    @Qualifier("jdbcTemplate3")
    JdbcTemplate jdbcTemplate;


    public Integer updateCounter(int chatId, int numberOfNewMessages, int profileId) {
        var numberOfExistingMessagesList = getNumberOfMessagesInDb(chatId, profileId);
        if (numberOfExistingMessagesList.size() > 0) {
            String query = "UPDATE counter SET count=? WHERE chat_id=?";
            var res = jdbcTemplate.update(
                query, numberOfNewMessages + numberOfExistingMessagesList.get(0), chatId
            );
        } else {
            String query = "INSERT into counter (chat_id,profile_id,count) values (?,?,?)";
            var res = jdbcTemplate.update(
                query, chatId,profileId, numberOfNewMessages
            );
        }
        return getNumberOfMessagesInDb(chatId,profileId).get(0);
    }

    public Integer getNumberOfMessages(int chatId, int profileId) {
        var res = getNumberOfMessagesInDb(chatId, profileId);
        return res.size() > 0 ? res.get(0) : 0;
    }

    private List<Integer> getNumberOfMessagesInDb(int chatId, int profileId) {
        String query = "SELECT count FROM counter WHERE chat_id=? AND profile_id=?";
        var res = jdbcTemplate.query(
            query, (rs, rowNum) ->
                rs.getInt("count"), chatId, profileId
        );
        return res;
    }

}
