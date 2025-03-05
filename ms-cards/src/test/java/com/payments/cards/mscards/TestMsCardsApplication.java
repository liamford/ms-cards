package com.payments.cards.mscards;

import org.springframework.boot.SpringApplication;

public class TestMsCardsApplication {

    public static void main(String[] args) {
        SpringApplication.from(MsCardsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
