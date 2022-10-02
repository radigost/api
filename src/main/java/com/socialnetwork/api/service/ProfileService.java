package com.socialnetwork.api.service;

import com.socialnetwork.api.v1.domain.ProfileDto;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileService {
    JdbcTemplate jdbcTemplate;

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ProfileService(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<ProfileDto> getProfiles() {
        String query = "SELECT * FROM socialnetwork.profile";
        return jdbcTemplate.query(
            query,
            (rs, rowNum) -> ProfileDto.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .yearsOld(rs.getInt("age"))
                .gender(rs.getString("gender"))
                .interests(rs.getString("interests"))
                .city(rs.getString("city"))
                .build()
        );
    }

    public ProfileDto getProfileById(int id) {
        String query = "SELECT * FROM socialnetwork.profile WHERE id=?";
        // TODO fix deprecated method
        return jdbcTemplate.queryForObject(
            query,
            new Object[] {id},
            (rs, rowNum) -> ProfileDto.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .yearsOld(rs.getInt("age"))
                .gender(rs.getString("gender"))
                .interests(rs.getString("interests"))
                .city(rs.getString("city"))
                .build()
        );
    }

    public boolean createProfile(ProfileDto profile) {
        log.info(profile.toString());

        var userId = profile.getOwnerId();
        var exists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM socialnetwork.profile WHERE owner_id=?", Integer.class, new Object[] {userId});
        if (exists == null || exists.equals(0)) {
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(profile);

            var result = namedParameterJdbcTemplate.update(
                "INSERT INTO socialnetwork.profile" +
                    " (first_name,last_name,age,gender,interests,city,owner_id)" +
                    " VALUES (:firstName,:lastName,:yearsOld,:gender,:interests,:city,:ownerId)",
                namedParameters
            );
            if (result == 1) {
                log.info(String.format("created profile for userId %d", userId));
                return true;
            }
        }
        return false;
    }
}
