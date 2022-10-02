package com.socialnetwork.api.service;

import com.socialnetwork.api.v1.domain.AuthData;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean authenticate(AuthData authData) {
        var encodedPassword = jdbcTemplate.queryForObject(
            "SELECT password FROM socialnetwork.user WHERE username=?",
            String.class,
            authData.getUsername()
        );
        return passwordEncoder.matches(authData.getPassword(), encodedPassword);
    }

    public boolean register(AuthData authData) {
        var username = authData.getUsername();
        if (username != null) {
            var exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM socialnetwork.user WHERE username=?", Integer.class, new Object[] {username});
            if (exists == null || exists.equals(0)) {
                var password = authData.getPassword();
                if (password != null) {
                    var result = jdbcTemplate.update(
                        "INSERT INTO socialnetwork.user (username, password) VALUES (?,?)",
                        username,
                        passwordEncoder.encode(password)
                    );
                    if (result == 1) {
                        System.out.printf(String.format("registered user %s", username));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
