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

import java.io.InputStream;

@Service
@Slf4j
public class SolacePublisher {

    private final JCSMPSession jcsmpSession;
    private final Schema cardSchema;
    private final ObjectMapper objectMapper;


    @Autowired
    public SolacePublisher(JCSMPSession jcsmpSession, ObjectMapper objectMapper) {
        this.jcsmpSession = jcsmpSession;
        this.objectMapper = objectMapper;
        this.cardSchema = loadSchema();
    }

    private Schema loadSchema() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schemas/card-schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            return SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON schema", e);
        }
    }

    public void publishCard(Card card, String topicName) throws JCSMPException {

        validateCard(card);
        Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
        XMLMessageProducer producer = jcsmpSession.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            @Override
            public void responseReceived(String messageID) {
                log.info("Producer received response for msg: {}" , messageID);
            }

            @Override
            public void handleError(String messageID, JCSMPException e, long timestamp) {
               log.warn("Producer received error for msg: {}" ,messageID , e);
            }
        });


        try {
            String cardJson = objectMapper.writeValueAsString(card);
            TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
            message.setCorrelationId(card.getId());
            SDTMap properties = JCSMPFactory.onlyInstance().createMap();
            properties.putString("application", "ms-cards");
            message.setProperties(properties);
            message.setApplicationMessageId(card.getId());
            message.setText(cardJson);
            producer.send(message, topic);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Card object to JSON", e);
        }
    }

    private void validateCard(Card card) {
        JSONObject cardJson = new JSONObject(card);
        cardSchema.validate(cardJson);
    }




}
