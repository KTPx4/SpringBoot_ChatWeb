package com.Px4.ChatAPI.config;


import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.models.account.ResetModel;
import com.Px4.ChatAPI.models.jwt.BlackListModel;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;


@Configuration

public class DBConfig {
    @Value("${spring.datasource.dbhost}")
    private String dbHost;

    @Value("${spring.datasource.dbport}")
    private String dbPort;

    @Value("${spring.datasource.dbname}")
    private String dbName;

    @Value("${spring.datasource.dbuser}")
    private String dbUser;
    @Value("${spring.datasource.dbpass}")
    private String dbPass;


    @Bean
    public MongoTemplate mongoTemplate() {
        String connectionString = String.format("mongodb://%s:%s", dbHost, dbPort); // connect to local db

//        String connectionString = String.format("mongodb+srv://%s:%s@dbfinal.th4yvbk.mongodb.net/?retryWrites=true&w=majority&appName=DbFinal", dbUser, dbPass); // connect to cloud db


        MongoTemplate mongoTemplate =new MongoTemplate(MongoClients.create(connectionString), dbName);

        // Config auto delete for black list jwt
        IndexOperations indexOps = mongoTemplate.indexOps(BlackListModel.class);
        Index index = new Index().on("createdAt", Sort.Direction.ASC).expire(BlackListModel.TIME_LIVE); // 7 day
        indexOps.ensureIndex(index);

        // Config auto delete for reset account token
        IndexOperations indexResetAcc = mongoTemplate.indexOps(ResetModel.class);
        Index index2 = new Index().on("createdAt", Sort.Direction.ASC).expire(ResetModel.TIME_LVIE); // 5 minute
        indexResetAcc.ensureIndex(index2);

        return mongoTemplate;
    }



}
