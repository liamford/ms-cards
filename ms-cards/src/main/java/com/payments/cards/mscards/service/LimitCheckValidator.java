package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.model.ValidationResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("limitCheckFunction")
public class LimitCheckValidator implements Function<Card, ValidationResponse> {


    private boolean checkLimit(Card card) {
        // Implement your limit check logic here
        return card.getAvailableBalance() > 0; // Example logic
    }

    @Override
    public ValidationResponse apply(Card card) {
        return ValidationResponse.builder()
                .isValid(checkLimit(card))
                .validation("LIMIT_CHECK")
                .build();
    }
}
