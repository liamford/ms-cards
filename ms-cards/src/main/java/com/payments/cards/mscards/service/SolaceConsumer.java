package com.payments.cards.mscards.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.cards.mscards.model.Card;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SolaceConsumer {

    private final JCSMPSession jcsmpSession;
    private final ObjectMapper objectMapper;

    @Autowired
    public SolaceConsumer(JCSMPSession jcsmpSession, ObjectMapper objectMapper) {
        this.jcsmpSession = jcsmpSession;
        this.objectMapper = objectMapper;
        initializeConsumer();
    }

    private void initializeConsumer() {
        try {
            Topic topic = JCSMPFactory.onlyInstance().createTopic("payments/credit-cards/initiate");
            XMLMessageConsumer consumer = jcsmpSession.getMessageConsumer(new XMLMessageListener() {
                @Override
                public void onReceive(BytesXMLMessage msg) {
                    try {
                        log.info("Consumer initialized for ID: {} and application: {}", msg.getCorrelationId(), msg.getProperties().get("application").toString());
                    } catch (SDTException e) {
                        log.error("Failed to get application property", e);
                    }
                    if (msg instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) msg;
                        log.info("Received message: " + textMessage.getText());
                        try {
                            Card card = objectMapper.readValue(textMessage.getText(), Card.class);
                            log.info("Converted message to Card: " + card);
                            // Process the Card object here
                        } catch (Exception e) {
                            log.error("Failed to convert message to Card", e);
                        }
                    } else {
                        log.warn("Received message of unsupported type.");
                    }
                }

                @Override
                public void onException(JCSMPException e) {
                    log.error("Consumer received exception: ", e);
                }
            });

            jcsmpSession.addSubscription(topic);
            consumer.start();
        } catch (JCSMPException e) {
            throw new RuntimeException("Failed to initialize Solace consumer", e);
        }
    }
}