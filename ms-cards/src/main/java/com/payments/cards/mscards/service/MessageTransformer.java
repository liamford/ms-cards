package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("transformFunction")
@Slf4j
public class MessageTransformer implements Function<Card, Notification> {

    @Override
    public Notification apply(Card card) {
        log.info("Received Transformer message: {}", card);
        return Notification.builder()
                .id(card.getId())
                .maskedCardNumber(card.getMaskedCardNumber())
                .status(card.getStatus())
                .cardholderName(card.getCardholderName())
                .build();
    }
}
