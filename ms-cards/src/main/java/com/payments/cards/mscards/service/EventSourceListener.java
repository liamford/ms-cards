package com.payments.cards.mscards.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.cards.mscards.model.EventSource;
import com.payments.cards.mscards.repository.EventSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class EventSourceListener {

    private final EventSourceRepository eventSourceRepository;

    public EventSourceListener(EventSourceRepository eventSourceRepository) {
        this.eventSourceRepository = eventSourceRepository;
    }


    @Bean
    public java.util.function.Consumer<Message<String>> eventSourceFunction() {
        return message -> {
            Object destination = message.getHeaders().get("solace_destination");
            String topic = destination != null ? destination.toString() : "UNKNOWN";
            String payload = message.getPayload();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(payload);
                String id = jsonNode.get("id").asText();
                log.info("Received response: {} from Topic: {} with ID: {}", payload, topic, id);
                eventSourceRepository.save(EventSource.builder()
                        .id(UUID.randomUUID().toString()).payload(payload).topic(topic).cardId(id).build());
            } catch (JsonProcessingException e) {
                log.error("Error processing JSON payload", e);
            }
        };
        }


}
