package com.socialnetwork.api.service;

import com.socialnetwork.api.v1.domain.UserDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String query = "SELECT * FROM socialnetwork.user WHERE username=?";
        var user = jdbcTemplate.queryForObject(query,
            (rs, rowNum) -> UserDto.builder().id(rs.getInt("id")).username(rs.getString("username"))
                .password(rs.getString("password")).role(rs.getString("role")).build(), username);
        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found.");
        }
        var grantedAuthority = new SimpleGrantedAuthority(user.getRole());
        return new User(user.getUsername(), user.getPassword(), List.of(grantedAuthority));
    }
}
