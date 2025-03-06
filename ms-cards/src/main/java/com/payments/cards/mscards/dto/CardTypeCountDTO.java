package com.payments.cards.mscards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CardTypeCountDTO {
    private final String cardType;
    private final int count;
}
