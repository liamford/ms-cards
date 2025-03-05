package com.payments.cards.mscards.repository;


import com.payments.cards.mscards.model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditCardRepository extends MongoRepository<Card, String> {
}
