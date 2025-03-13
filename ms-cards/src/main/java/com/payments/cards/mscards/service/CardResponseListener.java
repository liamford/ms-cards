package com.payments.cards.mscards.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.cards.mscards.model.CreditCheck;
import com.payments.cards.mscards.model.ValidationResponse;
import com.payments.cards.mscards.repository.CreditCheckRepository;
import com.payments.cards.mscards.swagger.model.CreditCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CardResponseListener {

    private final ObjectMapper objectMapper;
    private final CreditCheckRepository creditCheckRepository;
    private final CreditCardService creditCardService;

    public CardResponseListener(ObjectMapper objectMapper, CreditCheckRepository creditCheckRepository, CreditCardService creditCardService) {
        this.objectMapper = objectMapper;
        this.creditCheckRepository = creditCheckRepository;
        this.creditCardService = creditCardService;
    }

    @Bean
    public java.util.function.Consumer<String> responseFunction() {
        return message -> {
            log.info("Received response: {}", message);
            try {
                ValidationResponse response = objectMapper.readValue(message, ValidationResponse.class);
                if (response.isValid()) {
                    checkAndUpdateStatus(response);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
        }

    private void checkAndUpdateStatus(ValidationResponse response) {
        try {

            Optional<CreditCheck> creditCheck = creditCheckRepository.findById(response.getId());
            if(creditCheck.isPresent()) {
                if (creditCheck.get().isCreditStatusCheckStatus() && creditCheck.get().isLimitCheckStatus()) {
                    creditCardService.updateCardStatus(response.getId(), CreditCard.StatusEnum.ACTIVE.toString());
                    creditCheckRepository.save(creditCheck.get());
                } else {
                    log.warn("Credit check not found for card ID {}", response.getId());
                }
            }
            } catch (Exception e) {
                log.error("Error processing response: {}", e.getMessage());
            }
    }

}
