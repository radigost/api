package com.socialnetwork.api.service;

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
        if (numberOfExistingMessagesList > 0) {
            String query = "UPDATE counter SET count=? WHERE chat_id=? AND profile_id=?";
            jdbcTemplate.update(
                query, numberOfNewMessages + numberOfExistingMessagesList, chatId, profileId
            );
        } else {
            String query = "INSERT into counter (chat_id,profile_id,count) values (?,?,?)";
        }
        return getNumberOfMessagesInDb(chatId, profileId);
    }

    public Integer getNumberOfMessages(int chatId, int profileId) {
        var res = getNumberOfMessagesInDb(chatId, profileId);
        return res > 0 ? res : 0;
    }

    private Integer getNumberOfMessagesInDb(int chatId, int profileId) {
        String query = "SELECT count FROM counter WHERE chat_id=? AND profile_id=?";
        var res = jdbcTemplate.query(
            query, (rs, rowNum) ->
                rs.getInt("count"), chatId, profileId
        );
        return res.get(0);
    }

}
