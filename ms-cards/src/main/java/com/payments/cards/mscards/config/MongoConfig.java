package com.payments.cards.mscards.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUrl;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUrl); // Update with your MongoDB connection string
    }
}
