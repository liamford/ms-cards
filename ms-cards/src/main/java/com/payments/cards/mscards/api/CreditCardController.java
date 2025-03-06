package com.payments.cards.mscards.api;

import com.payments.cards.mscards.service.CreditCardService;
import com.payments.cards.mscards.swagger.model.CreditCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Slf4j
public class CreditCardController {
    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping("/cards")
    public ResponseEntity<CreditCard> createCreditCard(@Valid  @RequestBody CreditCard creditCard) {
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
    public ResponseEntity<CreditCard> updateCardStatus(@PathVariable String id, @Valid @RequestParam CreditCard.StatusEnum status) {
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

    @PutMapping("/cards/{id}")
    public ResponseEntity<CreditCard> updateCreditCard(@PathVariable String id, @Valid @RequestBody CreditCard creditCard) {
        return ResponseEntity.ok(creditCardService.updateCreditCard(id, creditCard));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<String> deleteCreditCard(@PathVariable String id) {
        creditCardService.deleteCreditCard(id);
        return ResponseEntity.ok("Credit card deleted successfully");
    }
}
