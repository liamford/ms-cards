package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("transformFunction")
@Slf4j
public class MessageTransformer implements Function<Card, Card> {

    @Override
    public Card apply(Card card) {
        log.info("Received Transformer message: {}", card);
        card.setCardholderName(card.getCardholderName().toUpperCase());
        return card;
    }
}
