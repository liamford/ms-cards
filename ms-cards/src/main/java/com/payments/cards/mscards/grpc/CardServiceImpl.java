package com.payments.cards.mscards.grpc;

import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Status;
import com.payments.cards.mscards.service.CreditCardService;
import com.payments.cards.mscards.service.CryptoUtil;
import com.payments.cards.mscards.swagger.model.CreditCard;
import io.grpc.Metadata;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
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
    public void getCardDetails(CardServiceProto.CardIdRequest request,
                               StreamObserver<CardServiceProto.CardDetailsResponse> responseObserver) {
        try {
            String cardId = request.getCardId();

            // ✅ Validate input
            if (cardId == null || cardId.isEmpty()) {
                throw buildInvalidArgumentException("cardId", "Card ID cannot be empty.");
            }

            // ✅ Fetch from repository
            Optional<CreditCard> cardById = creditCardService.getCreditCardById(cardId);

            if (cardById.isPresent()) {
                CreditCard card = cardById.get();

                // ✅ Build successful response
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
                responseObserver.onCompleted();
            } else {
                throw buildNotFoundException(cardId);
            }

        } catch (StatusException | StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            responseObserver.onError(buildInternalError(e));
        }
    }

    // 🧰 Helper: Build INVALID_ARGUMENT error using StatusProto
    private StatusRuntimeException buildInvalidArgumentException(String field, String description) {
        BadRequest.FieldViolation violation = BadRequest.FieldViolation.newBuilder()
                .setField(field)
                .setDescription(description)
                .build();

        BadRequest badRequest = BadRequest.newBuilder()
                .addFieldViolations(violation)
                .build();

        Status status = Status.newBuilder()
                .setCode(io.grpc.Status.Code.INVALID_ARGUMENT.value())
                .setMessage("Invalid input.")
                .addDetails(Any.pack(badRequest))
                .build();

        return StatusProto.toStatusRuntimeException(status);
    }

    // 🧰 Helper: Build NOT_FOUND error with Metadata
    private StatusException buildNotFoundException(String cardId) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER), "CARD_NOT_FOUND");
        metadata.put(Metadata.Key.of("trace-id", Metadata.ASCII_STRING_MARSHALLER), generateTraceId());

        io.grpc.Status status = io.grpc.Status.NOT_FOUND
                .withDescription("Card with ID " + cardId + " not found.");

        return status.asException(metadata);
    }

    // 🧰 Helper: Build INTERNAL error for unexpected issues
    private StatusException buildInternalError(Exception e) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("trace-id", Metadata.ASCII_STRING_MARSHALLER), generateTraceId());

        io.grpc.Status status = io.grpc.Status.INTERNAL
                .withDescription("Unexpected error: " + e.getMessage())
                .withCause(e);

        return status.asException(metadata);
    }

    // 🏷️ Helper: Generate trace ID for debugging (example)
    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }
}