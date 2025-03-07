package com.payments.cards.mscards.mapper;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.swagger.model.CreditCard;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreditCardMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public Card toEntity(CreditCard creditCard) {
        return Card.builder()
                .id(Optional.ofNullable(creditCard.getCardId()).orElse(UUID.randomUUID().toString()))
                .cardholderName(creditCard.getCardholderName())
                .cardNumber(creditCard.getCardNumber())
                .maskedCardNumber(maskCreditCardNumber(creditCard.getCardNumber()))
                .expirationDate(creditCard.getExpirationDate())
                .cardType(creditCard.getCardType().toString())
                .creditLimit(Optional.ofNullable(creditCard.getCreditLimit()).map(Float::doubleValue).orElse(0.0))
                .availableBalance(Optional.ofNullable(creditCard.getAvailableBalance()).map(Float::doubleValue).orElse(0.0))
                .status(creditCard.getStatus().toString())
                .createdAt(creditCard.getCreatedAt() != null ? Instant.parse(creditCard.getCreatedAt()) : Instant.now())
                .lastUpdated(Instant.now())
                .build();
    }

    public CreditCard toDto(Card card) {
        return new CreditCard()
                .cardId(card.getId())
                .cardholderName(card.getCardholderName())
                .cardNumber(card.getMaskedCardNumber())
                .expirationDate(card.getExpirationDate())
                .cardType(card.getCardType() != null ? CreditCard.CardTypeEnum.fromValue(card.getCardType()) : null)
                .creditLimit(card.getCreditLimit() != null ? card.getCreditLimit().floatValue() : 0.0f)
                .availableBalance(card.getAvailableBalance() != null ? card.getAvailableBalance().floatValue() : 0.0f)
                .status(card.getStatus() != null ? CreditCard.StatusEnum.fromValue(card.getStatus()) : CreditCard.StatusEnum.ACTIVE)
                .createdAt(card.getCreatedAt() != null ? formatInstant(card.getCreatedAt()) : null)
                .lastUpdated(card.getLastUpdated() != null ? formatInstant(card.getLastUpdated()) : null);
    }

    private String formatInstant(Instant instant) {
        return instant != null ? FORMATTER.format(instant.atOffset(ZoneOffset.UTC)) : null;
    }


    public String maskCreditCardNumber(String creditCardNumber) {
        if (creditCardNumber == null || creditCardNumber.length() < 4) {
            throw new IllegalArgumentException("Invalid credit card number");
        }
        return "**** **** **** " + creditCardNumber.substring(creditCardNumber.length() - 4);
    }
}