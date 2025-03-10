package com.payments.cards.mscards.service;

import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.model.Notification;
import com.solacesystems.jcsmp.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("transformFunction")
@Slf4j
public class MessageTransformer implements Function<Message<Card>, Notification> {

    @Override
    public Notification apply(Message<Card> message) {
        Card card = message.getPayload();
        Topic topic = message.getHeaders().get("solace_destination", Topic.class);
        String topicName = (topic != null) ? topic.getName() : "UNKNOWN";
        log.info("Received Transformer message: {}", card);
        return Notification.builder()
                .id(card.getId())
                .maskedCardNumber(card.getMaskedCardNumber())
                .status(card.getStatus())
                .cardholderName(card.getCardholderName())
                .action(determineAction(topicName))
                .build();
    }

    private String determineAction(String topic) {
        return switch (topic) {
            case "payments/credit-cards/initiate" -> "CREATE";
            case "payments/credit-cards/updated" -> "UPDATE";
            case "payments/credit-cards/deleted" -> "DELETE";
            default -> "UNKNOWN";
        };
    }
}
