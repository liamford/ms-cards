package com.payments.cards.mscards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableMongoRepositories
@EnableWebMvc
@EnableCaching
public class MsCardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCardsApplication.class, args);
    }

}
