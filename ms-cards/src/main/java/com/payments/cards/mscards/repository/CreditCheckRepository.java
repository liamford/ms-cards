package com.payments.cards.mscards.repository;


import com.payments.cards.mscards.model.CreditCheck;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditCheckRepository extends MongoRepository<CreditCheck, String> {

}
