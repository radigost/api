package com.socialnetwork.api.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class SpringJdbcMessagesConfig {


    @Value("${persistence.messages.url}")
    private String jdbcUrl;

    @Value("${persistence.messages.username}")
    private String username;

    @Value("${persistence.messages.password}")
    private String password;

    public DataSource dataSourceMessages() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "jdbcTemplate3")
    public JdbcTemplate jdbcTemplate3() {
        return new JdbcTemplate(dataSourceMessages());
    }

}
