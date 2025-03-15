package com.payments.cards.mscards.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class CardServiceClient {
    private final CardServiceGrpc.CardServiceBlockingStub blockingStub;

    public CardServiceClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        blockingStub = CardServiceGrpc.newBlockingStub(channel);
    }

    public CardServiceProto.CardDetailsResponse getCardDetails(String cardId) {
        CardServiceProto.CardIdRequest request = CardServiceProto.CardIdRequest.newBuilder().setCardId(cardId).build();
        return blockingStub.getCardDetails(request);
    }
}
