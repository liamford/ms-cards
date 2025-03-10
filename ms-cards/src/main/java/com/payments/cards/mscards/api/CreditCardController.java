package com.payments.cards.mscards.api;

import com.payments.cards.mscards.dto.CardTypeCountDTO;
import com.payments.cards.mscards.service.AiQueryService;
import com.payments.cards.mscards.service.CreditCardService;
import com.payments.cards.mscards.swagger.model.CreditCard;
import com.solacesystems.jcsmp.JCSMPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class CreditCardController {
    private final CreditCardService creditCardService;
    private final AiQueryService aiQueryService;

    public CreditCardController(CreditCardService creditCardService, AiQueryService aiQueryService) {
        this.creditCardService = creditCardService;
        this.aiQueryService = aiQueryService;
    }


    @PostMapping("/cards")
    public ResponseEntity<CreditCard> createCreditCard(@Valid  @RequestBody CreditCard creditCard) throws JCSMPException {
        log.info("Creating credit card: {}", creditCard);
        return ResponseEntity.ok(creditCardService.createCreditCard(creditCard));
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<CreditCard>> getAllCreditCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all credit cards");
        return ResponseEntity.ok(creditCardService.getAllCreditCards(page, size));
    }

    @PatchMapping("/cards/{id}/status")
    public ResponseEntity<CreditCard> updateCardStatus(@PathVariable String id, @Valid @RequestParam CreditCard.StatusEnum status) throws JCSMPException {
        Optional<CreditCard> updatedCreditCard = creditCardService.updateCardStatus(id, status.toString());
        return updatedCreditCard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<CreditCard> getCreditCardById(@PathVariable String id) {
        Optional<CreditCard> creditCard = creditCardService.getCreditCardById(id);
        return creditCard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cards/report/card-type")
    public ResponseEntity<List<CardTypeCountDTO>> getCardTypeReport() {
       return ResponseEntity.ok(creditCardService.getCreditCardCountsByType());
    }

    @PutMapping("/cards/{id}")
    public ResponseEntity<CreditCard> updateCreditCard(@PathVariable String id, @Valid @RequestBody CreditCard creditCard) {
        return ResponseEntity.ok(creditCardService.updateCreditCard(id, creditCard));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<String> deleteCreditCard(@PathVariable String id) throws JCSMPException {
        creditCardService.deleteCreditCard(id);
        return ResponseEntity.ok("Credit card deleted successfully");
    }

    @PostMapping("/cards/query")
    public ResponseEntity<List<CreditCard>> queryCards(@RequestBody String naturalLanguageQuery) {
        return ResponseEntity.ok(aiQueryService.executeQuery(naturalLanguageQuery));
    }

    @GetMapping("/cards/search")
    public ResponseEntity<List<CreditCard>> searchCreditCards(@RequestParam String query) {
        log.info("Searching credit cards with query: {}", query);
        return ResponseEntity.ok(creditCardService.searchCreditCardsByCardHolderName(query));
    }
}
