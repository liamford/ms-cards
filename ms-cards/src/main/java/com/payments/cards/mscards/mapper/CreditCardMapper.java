package com.payments.cards.mscards.mapper;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.swagger.model.CreditCard;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class CreditCardMapper {

    public Card toEntity(CreditCard creditCard) {
        return Card.builder()
                .id(creditCard.getCardId())
                .cardholderName(creditCard.getCardholderName())
                .cardNumber(creditCard.getCardNumber())
                .maskedCardNumber(creditCard.getCardNumber())
                .expirationDate(creditCard.getExpirationDate())
                .cardType(creditCard.getCardType() != null ? creditCard.getCardType().toString() : null)
                .creditLimit(Optional.ofNullable(creditCard.getCreditLimit()).map(Float::doubleValue).orElse(0.0))
                .availableBalance(Optional.ofNullable(creditCard.getAvailableBalance()).map(Float::doubleValue).orElse(0.0))
                .status(creditCard.getStatus() != null ? creditCard.getStatus().toString() : "ACTIVE")
                .createdAt(creditCard.getCreatedAt() != null ? creditCard.getCreatedAt().toInstant() : Instant.now())
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
                .createdAt(card.getCreatedAt() != null ? OffsetDateTime.ofInstant(card.getCreatedAt(), java.time.ZoneOffset.UTC) : null)
                .lastUpdated(card.getLastUpdated() != null ? OffsetDateTime.ofInstant(card.getLastUpdated(), java.time.ZoneOffset.UTC) : null);
    }
}
