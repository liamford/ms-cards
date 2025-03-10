package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.model.ValidationResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("statusCheckFunction")
public class CreditStatusValidator implements Function<Card, ValidationResponse> {


    private boolean checkStatus(Card card) {
        return "ACTIVE".equals(card.getStatus());
    }

    @Override
    public ValidationResponse apply(Card card) {
        return ValidationResponse.builder()
                .isValid(checkStatus(card))
                .validation("STATUS_CHECK")
                .build();
    }
}
