package com.payments.cards.mscards.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "eventSource")
public class EventSource {

    @Id
    private String id;

    @NotBlank(message = "cardId is required")
    @TextIndexed
    private String cardId;

    @NotBlank(message = "Topic is required")
    @TextIndexed
    private String topic;

    @NotBlank(message = "Payload is required")
    private String payload;
}
