package com.payments.cards.mscards.config;

import com.payments.cards.mscards.grpc.CardServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcConfig {

    @Value("${grpc.server.host:localhost}")
    private String grpcServerHost;

    @Value("${grpc.server.port:9090}")
    private int grpcServerPort;

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public Server grpcServer(CardServiceImpl cardService) throws IOException {
        Server server = ServerBuilder.forPort(grpcServerPort)
                .addService(cardService)
                .build();
        server.start();
        return server;
    }
}