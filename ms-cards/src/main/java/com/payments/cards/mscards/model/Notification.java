package com.payments.cards.mscards.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private String id;
    private String maskedCardNumber;
    private String status;
    private String cardholderName;
    private String action;
}
