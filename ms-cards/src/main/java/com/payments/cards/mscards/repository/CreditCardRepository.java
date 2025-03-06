package com.payments.cards.mscards.repository;


import com.payments.cards.mscards.dto.CardTypeCountDTO;
import com.payments.cards.mscards.model.Card;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CreditCardRepository extends MongoRepository<Card, String> {

    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$cardType', 'count': { '$sum': 1 } } }",
            "{ '$project': { 'cardType': '$_id', 'count': 1, '_id': 0 } }"
    })
    List<CardTypeCountDTO> countCreditCardsByCardType();


}
