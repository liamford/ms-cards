package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.model.CreditCheck;
import com.payments.cards.mscards.model.ValidationResponse;
import com.payments.cards.mscards.repository.CreditCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("limitCheckFunction")
@Slf4j
public class LimitCheckValidator implements Function<Card, ValidationResponse> {

    private final CreditCheckRepository creditCheckRepository;

    public LimitCheckValidator(CreditCheckRepository creditCheckRepository) {
        this.creditCheckRepository = creditCheckRepository;
    }

    private boolean checkLimit(Card card) {
        // Implement your limit check logic here
        boolean result = card.getAvailableBalance() > 0;

        CreditCheck creditCheck = creditCheckRepository.findById(card.getId()).orElse(CreditCheck.builder().id(card.getId()).build());
        creditCheck.setLimitCheckStatus(result);
        creditCheckRepository.save(creditCheck);
        log.info("Limit check result for card {} = : {}",card.getId(), result);
        return  result;// Example logic
    }

    @Override
    public ValidationResponse apply(Card card) {
        return ValidationResponse.builder()
                .isValid(checkLimit(card))
                .id(card.getId())
                .validation("LIMIT_CHECK")
                .build();
    }
}
