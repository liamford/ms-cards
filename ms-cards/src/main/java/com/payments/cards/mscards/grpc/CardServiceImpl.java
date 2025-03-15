package com.payments.cards.mscards.grpc;

import com.payments.cards.mscards.service.CreditCardService;
import com.payments.cards.mscards.service.CryptoUtil;
import com.payments.cards.mscards.swagger.model.CreditCard;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardServiceImpl extends CardServiceGrpc.CardServiceImplBase {

    private final CreditCardService creditCardService;
    private final CryptoUtil cryptoUtil;

    public CardServiceImpl(CreditCardService creditCardService, CryptoUtil cryptoUtil) {
        this.creditCardService = creditCardService;
        this.cryptoUtil = cryptoUtil;
    }


    @Override
    public void getCardDetails(CardServiceProto.CardIdRequest request, StreamObserver<CardServiceProto.CardDetailsResponse> responseObserver) {
        String cardId = request.getCardId();
        Optional<CreditCard> cardById = creditCardService.getCreditCardById(cardId);

        if (cardById.isPresent()) {
            CreditCard card = cardById.get();
            CardServiceProto.CardDetailsResponse response = CardServiceProto.CardDetailsResponse.newBuilder()
                    .setCardId(card.getCardId())
                    .setCardholderName(card.getCardholderName())
                    .setCardNumber(cryptoUtil.encrypt(card.getCardNumber()))
                    .setExpirationDate(card.getExpirationDate())
                    .setCardType(card.getCardType().toString())
                    .setCreditLimit(card.getCreditLimit())
                    .setAvailableBalance(card.getAvailableBalance())
                    .setStatus(card.getStatus().toString())
                    .setCreatedAt(card.getCreatedAt().toString())
                    .setLastUpdated(card.getLastUpdated().toString())
                    .build();
            responseObserver.onNext(response);
        } else {
            CardServiceProto.CardDetailsResponse errorResponse = CardServiceProto.CardDetailsResponse.newBuilder()
                    .setError("Card not found")
                    .build();
            responseObserver.onNext(errorResponse);
        }
        responseObserver.onCompleted();
    }

}
