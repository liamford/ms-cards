package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.model.CreditCheck;
import com.payments.cards.mscards.model.ValidationResponse;
import com.payments.cards.mscards.repository.CreditCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.function.Function;

@Component("statusCheckFunction")
@Slf4j
public class CreditStatusValidator implements Function<Card, ValidationResponse> {

    private final CreditCheckRepository creditCheckRepository;

    public CreditStatusValidator(CreditCheckRepository creditCheckRepository) {
        this.creditCheckRepository = creditCheckRepository;
    }


    private boolean checkStatus(Card card) {
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth expiryYearMonth = YearMonth.parse(card.getExpirationDate());
        return currentYearMonth.isBefore(expiryYearMonth);
    }

    @Override
    public ValidationResponse apply(Card card) {
        boolean result = checkStatus(card);
        CreditCheck creditCheck = creditCheckRepository.findById(card.getId()).orElse(CreditCheck.builder().id(card.getId()).build());
        creditCheck.setCreditStatusCheckStatus(result);
        creditCheckRepository.save(creditCheck);
        log.info("Credit check result for card {} = : {}",card.getId(), result);
        return ValidationResponse.builder()
                .isValid(result)
                .validation("STATUS_CHECK")
                .id(card.getId())
                .build();
    }
}
