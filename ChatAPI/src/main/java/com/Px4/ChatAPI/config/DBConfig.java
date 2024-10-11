package com.Px4.ChatAPI.config;


import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class DBConfig {
    @Value("${spring.datasource.dbhost}")
    private String dbHost;

    @Value("${spring.datasource.dbport}")
    private String dbPort;

    @Value("${spring.datasource.dbname}")
    private String dbName;


    @Bean
    public MongoTemplate mongoTemplate() {
        String connectionString = String.format("mongodb://%s:%s", dbHost, dbPort);
        return new MongoTemplate(MongoClients.create(connectionString), dbName);
    }
}
