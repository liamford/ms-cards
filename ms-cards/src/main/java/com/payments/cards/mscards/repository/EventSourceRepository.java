package com.payments.cards.mscards.repository;


import com.payments.cards.mscards.model.EventSource;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventSourceRepository extends MongoRepository<EventSource, String> {

}
