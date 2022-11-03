package com.socialnetwork.api.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Slf4j
public class SpringJdbcReplicaConfig {

    @Value("${persistence.replica.jdbcUrl}")
    private String jdbcUrl;

    @Value("${persistence.replica.username}")
    private String username;

    @Value("${persistence.replica.password}")
    private String password;

    public DataSource dataSourceReplica() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "jdbcTemplate2")
    public JdbcTemplate jdbcTemplate2() {
        return new JdbcTemplate(dataSourceReplica());
    }


}
