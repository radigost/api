package com.socialnetwork.api.service;

import com.socialnetwork.api.v1.domain.ProfileDto;
import java.util.List;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileService {

    @Autowired
    @Qualifier("jdbcTemplate1")
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    @Qualifier("jdbcTemplate2")
    private JdbcTemplate jdbcTemplate2;

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ProfileService(@Qualifier("db_main") DataSource dataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<ProfileDto> getProfiles(@NonNull String firstName, @NonNull String lastName) {
        String query = "SELECT * FROM socialnetwork.profile" +
            " WHERE profile.first_name LIKE ?" +
            " AND profile.last_name LIKE ?" +
            "ORDER BY id";
        return jdbcTemplate2.query(query,
            (rs, rowNum) -> ProfileDto.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .yearsOld(rs.getInt("age"))
                .gender(rs.getString("gender"))
                .interests(rs.getString("interests"))
                .city(rs.getString("city"))
                .ownerId(rs.getInt("owner_id"))
                .build(), firstName, lastName);
    }

    public List<ProfileDto> getProfiles() {
        String query = "SELECT * FROM socialnetwork.profile";
        return jdbcTemplate2.query(query,
            (rs, rowNum) -> ProfileDto.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .yearsOld(rs.getInt("age"))
                .gender(rs.getString("gender"))
                .interests(rs.getString("interests"))
                .city(rs.getString("city"))
                .ownerId(rs.getInt("owner_id"))
                .build());
    }

    public ProfileDto getProfileById(int id) {
        String query = "SELECT * FROM socialnetwork.profile WHERE id=?";
        return jdbcTemplate2.queryForObject(query,
            (rs, rowNum) -> ProfileDto.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .yearsOld(rs.getInt("age"))
                .gender(rs.getString("gender"))
                .interests(rs.getString("interests"))
                .city(rs.getString("city"))
                .ownerId(rs.getInt("owner_id"))
                .build(), id);
    }

    public boolean createProfile(ProfileDto profile) {
        log.info(profile.toString());

        var userId = profile.getOwnerId();
        var exists =
            jdbcTemplate1.queryForObject("SELECT COUNT(*) FROM socialnetwork.profile WHERE owner_id=?", Integer.class,
                userId);
        if (exists == null || exists.equals(0)) {
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(profile);

            var result = namedParameterJdbcTemplate.update(
                "INSERT INTO socialnetwork.profile" + " (first_name,last_name,age,gender,interests,city,owner_id)" +
                    " VALUES (:firstName,:lastName,:yearsOld,:gender,:interests,:city,:ownerId)", namedParameters);
            if (result == 1) {
                log.info(String.format("Created profile for userId %d", userId));
                return true;
            }
        }
        return false;
    }
}
