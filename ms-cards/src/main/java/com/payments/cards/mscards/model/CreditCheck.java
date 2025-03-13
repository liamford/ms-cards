package com.payments.cards.mscards.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "creditChecks") // Maps to MongoDB collection "creditCards"
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCheck {

    @Id
    private String id;  // MongoDB _id field
    private boolean limitCheckStatus;
    private boolean CreditStatusCheckStatus;
}
