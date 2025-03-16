package com.payments.cards.mscards.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.cards.mscards.model.Card;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;

@Slf4j
@Service
public class SolacePublisher {

    private final JCSMPSession jcsmpSession;
    private final Schema cardSchema = loadSchema();
    private final ObjectMapper objectMapper;
    private XMLMessageProducer producer;

    @Autowired
    public SolacePublisher(JCSMPSession jcsmpSession, ObjectMapper objectMapper) {
        this.jcsmpSession = jcsmpSession;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            this.producer = jcsmpSession.getMessageProducer(new JCSMPStreamingPublishCorrelatingEventHandler() {
                @Override
                public void responseReceivedEx(Object correlationKey) {
                    log.info("Producer received response for msg with correlationKey: {}", correlationKey);
                }

                @Override
                public void handleErrorEx(Object correlationKey, JCSMPException e, long timestamp) {
                    log.error("Producer error for msg with correlationKey: {} - {}", correlationKey, e.getMessage(), e);
                }
            });
        } catch (JCSMPException e) {
            log.error("Failed to initialize XMLMessageProducer", e);
            throw new RuntimeException("Failed to initialize XMLMessageProducer", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (producer != null) {
            producer.close();
            log.info("XMLMessageProducer closed");
        }
    }

    private Schema loadSchema() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schemas/card-schema.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Schema file not found");
            }
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            return SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            log.error("Failed to load JSON schema", e);
            throw new RuntimeException("Failed to load JSON schema", e);
        }
    }

    public void publishCard(Card card, String topicName) {
        validateCard(card);

        try {
            Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
            String cardJson = objectMapper.writeValueAsString(card);

            TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
            message.setCorrelationId(card.getId());
            message.setText(cardJson);

            // Use session-independent message ownership
            SDTMap properties = JCSMPFactory.onlyInstance().createMap();
            properties.putString("application", "ms-cards");
            message.setProperties(properties);

            producer.send(message, topic);
            log.info("Message published to topic '{}' with correlationKey '{}'", topicName, card.getId());

        } catch (JCSMPException e) {
            log.error("Failed to publish message to topic '{}'", topicName, e);
            throw new RuntimeException("Failed to publish card message", e);
        } catch (Exception e) {
            log.error("Failed to serialize and publish card", e);
            throw new RuntimeException("Failed to serialize card object", e);
        }
    }

    public void verifyCreditCard(Card card) {
        try {
            Topic topic = JCSMPFactory.onlyInstance().createTopic("payments/credit-cards/validate");
            String cardJson = objectMapper.writeValueAsString(card);

            TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
            message.setCorrelationId(card.getId());
            message.setText(cardJson);

            // Use session-independent message ownership
            SDTMap properties = JCSMPFactory.onlyInstance().createMap();
            properties.putString("application", "ms-cards");
            message.setProperties(properties);

            producer.send(message, topic);
            log.info("Verification request sent for card ID: {}", card.getId());

        } catch (JCSMPException e) {
            log.error("Failed to send verification request for card ID: {}", card.getId(), e);
            throw new RuntimeException("Failed to send verification request", e);
        } catch (Exception e) {
            log.error("Failed to serialize card for verification", e);
            throw new RuntimeException("Failed to serialize card object for verification", e);
        }
    }

    private void validateCard(Card card) {
        try {
            String cardJson = objectMapper.writeValueAsString(card);
            JSONObject cardJsonObject = new JSONObject(cardJson);
            cardSchema.validate(cardJsonObject);
        } catch (Exception e) {
            log.error("Card validation failed for card ID: {}", card.getId(), e);
            throw new RuntimeException("Card validation failed: " + e.getMessage(), e);
        }
    }
}