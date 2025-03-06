package com.payments.cards.mscards.repository;


import com.payments.cards.mscards.dto.CardTypeCountDTO;
import com.payments.cards.mscards.model.Card;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CreditCardRepository extends MongoRepository<Card, String> {

    @Cacheable("cardTypeCount")
    @Aggregation(pipeline = {
            "{ '$match': { 'status': 'ACTIVE' } }",
            "{ '$group': { '_id': '$cardType', 'count': { '$sum': 1 } } }",
            "{ '$project': { 'cardType': '$_id', 'count': 1, '_id': 0 } }"
    })
    List<CardTypeCountDTO> countCreditCardsByCardType();

    @Query(value = "?0")
    List<Card> findByCustomQuery(String query);


}
