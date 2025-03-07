package com.payments.cards.mscards.service;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChangeStreamService {

    private final MongoClient mongoClient;
    private final CreditCardService creditCardService;

    public ChangeStreamService(MongoClient mongoClient, CreditCardService creditCardService) {
        this.mongoClient = mongoClient;
        this.creditCardService = creditCardService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void watchChanges() {
        MongoDatabase database = mongoClient.getDatabase("domestic_payments"); // Update with your database name
        MongoCollection<Document> collection = database.getCollection("creditCards");

        collection.watch().forEach((ChangeStreamDocument<Document> change) -> {
            log.info("Change detected: {}", change);
            handleEvent(change);
        });
    }

    private void handleEvent(ChangeStreamDocument<Document> change) {
        // Handle the change event (e.g., log it, update a cache, etc.)
        log.info("Handling change event: {}", change);
        // Example: If the change is an insert, log the new document
        if (change.getOperationType() == com.mongodb.client.model.changestream.OperationType.INSERT) {
            Document fullDocument = change.getFullDocument();
            log.info("New document inserted: {}", fullDocument.toJson());
        }
    }



}
