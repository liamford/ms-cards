package com.payments.cards.mscards.service;

import com.payments.cards.mscards.dto.CardTypeCountDTO;
import com.payments.cards.mscards.mapper.CreditCardMapper;
import com.payments.cards.mscards.model.Card;
import com.payments.cards.mscards.repository.CreditCardRepository;
import com.payments.cards.mscards.swagger.model.CreditCard;
import com.solacesystems.jcsmp.JCSMPException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditCardService {

    private final CreditCardRepository cardRepository;
    private final CreditCardMapper creditCardMapper;
    private final SolacePublisher solacePublisher;
    private final String CARD_INITIATE_TOPIC = "payments/credit-cards/initiate";
    private final String CARD_UPDATE_TOPIC = "payments/credit-cards/updated";
    private final String CARD_DELETE_TOPIC = "payments/credit-cards/deleted";

    public CreditCardService(CreditCardRepository cardRepository, CreditCardMapper creditCardMapper, SolacePublisher solacePublisher) {
        this.cardRepository = cardRepository;
        this.creditCardMapper = creditCardMapper;
        this.solacePublisher = solacePublisher;
    }

    public List<CardTypeCountDTO> getCreditCardCountsByType() {
        return cardRepository.countCreditCardsByCardType();
    }


    @CacheEvict(value = "cardTypeCount", allEntries = true)
    public CreditCard createCreditCard(CreditCard creditCard) throws JCSMPException {
        Card cardEntity = creditCardMapper.toEntity(creditCard);
        Card savedCard = cardRepository.save(cardEntity);
        solacePublisher.publishCard(savedCard,CARD_INITIATE_TOPIC);
        return creditCardMapper.toDto(savedCard);
    }

    public Page<CreditCard> getAllCreditCards(int page, int size) {
        return cardRepository.findAll(PageRequest.of(page, size))
                .map(creditCardMapper::toDto);
    }

    public Optional<CreditCard> getCreditCardById(String id) {
        return cardRepository.findById(id).map(creditCardMapper::toDto);
    }

    @CacheEvict(value = "cardTypeCount", allEntries = true)
    public CreditCard updateCreditCard(String id, CreditCard updatedCreditCard) {
        return cardRepository.findById(id)
                .map(existingCard -> {
                    Card updatedCard = creditCardMapper.toEntity(updatedCreditCard);
                    updatedCard.setId(id);
                    updatedCard.setLastUpdated(existingCard.getLastUpdated());
                    CreditCard creditCard =  creditCardMapper.toDto(cardRepository.save(updatedCard));
                    try {
                        solacePublisher.publishCard(updatedCard,CARD_UPDATE_TOPIC);
                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }
                    return creditCard;
                })
                .orElseThrow(() -> new RuntimeException("Credit card not found"));
    }

    @CacheEvict(value = "cardTypeCount", allEntries = true)
    public void deleteCreditCard(String id) throws JCSMPException{
        Card removedCard = cardRepository.findById(id).orElseThrow(() -> new RuntimeException("Credit card not found"));
        cardRepository.deleteById(id);
        solacePublisher.publishCard(removedCard,CARD_DELETE_TOPIC);
    }

    @CacheEvict(value = "cardTypeCount", allEntries = true)
    public Optional<CreditCard> updateCardStatus(String id, String status) {
        return cardRepository.findById(id)
                .map(existingCard -> {
                    existingCard.setStatus(status);
                    existingCard.setLastUpdated(Instant.now());
                    Card savedCard = cardRepository.save(existingCard);
                    try {
                        solacePublisher.publishCard(savedCard,CARD_UPDATE_TOPIC);
                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }
                    return creditCardMapper.toDto(savedCard);
                });
    }

    public List<CreditCard> searchCreditCardsByCardHolderName(String searchText) {
        return cardRepository.searchByCardHolderName(searchText)
                .stream()
                .map(creditCardMapper::toDto)
                .collect(Collectors.toList());
    }
}
