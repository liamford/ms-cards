package com.payments.cards.mscards.config;

import com.payments.cards.mscards.grpc.CardServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Bean
    public Server grpcServer(CardServiceImpl cardService) throws IOException {
        Server server = ServerBuilder.forPort(9090)
                .addService(cardService)
                .build();
        server.start();
        return server;
    }
}
