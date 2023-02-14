package com.socialnetwork.api.service;

import com.socialnetwork.api.v1.domain.MessageDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {
    @Autowired
    @Qualifier("jdbcTemplate3")
    JdbcTemplate jdbcTemplate;


    public List<String> getListOfRooms(int userId) {
        String query = "SELECT id FROM rooms WHERE ?=ANY(participants)";
        var res =  jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                rs.getString("id"),
            userId);
        return res;

    }

    public List<MessageDto> getMessages(int roomId) {
        String query = "SELECT * FROM messages WHERE roomId=?";
        return jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                MessageDto.builder()
                    .text(rs.getString("text"))
                    .id(rs.getString("id"))
                    .timestamp(rs.getString("timestamp"))
                    .build(),
            roomId);
    }

}
