package com.payments.cards.mscards.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.time.Instant;

@Document(collection = "creditCards") // Maps to MongoDB collection "creditCards"
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    @Id
    private String id;  // MongoDB _id field

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    @Pattern(regexp = "\\d{16}", message = "Card number must contain only digits")
    @Indexed(unique = true) // Creates a unique index on the field
    private String cardNumber;

    @NotBlank(message = "Expiration date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Expiration date must be in YYYY-MM format")
    private String expirationDate;

    @NotNull(message = "Card type is required")
    private String cardType;

    @NotNull(message = "Credit limit is required")
    @Min(value = 1, message = "Credit limit must be greater than 0")
    private Double creditLimit;

    @NotNull(message = "Available balance is required")
    private Double availableBalance;

    @NotBlank(message = "Card status is required")
    private String status;

    private String maskedCardNumber;
    private Instant createdAt;
    private Instant lastUpdated;
}
