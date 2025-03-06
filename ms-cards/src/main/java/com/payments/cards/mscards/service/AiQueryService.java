package com.payments.cards.mscards.service;

import com.payments.cards.mscards.mapper.CreditCardMapper;
import com.payments.cards.mscards.repository.CreditCardRepository;
import com.payments.cards.mscards.swagger.model.CreditCard;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiQueryService {

    private final OpenAiService openAiService;
    private final CreditCardRepository cardRepository;
    private final CreditCardMapper creditCardMapper;
    public AiQueryService(@Value("${openai.api.key}") String apiKey, CreditCardRepository cardRepository, CreditCardMapper creditCardMapper) {
        this.openAiService = new OpenAiService(apiKey);
        this.cardRepository = cardRepository;
        this.creditCardMapper = creditCardMapper;
    }

    private String generateMongoQuery(String userQuery) {
        String prompt = "Convert the following natural language into a MongoDB query:\n\n" +
                "User input: \"" + userQuery + "\"\n" +
                "MongoDB query:";

        CompletionRequest request = CompletionRequest.builder()
                .model("gpt-4")  // Use "gpt-4" or "gpt-3.5-turbo"
                .prompt(prompt)
                .maxTokens(50)
                .temperature(0.2)
                .build();

        CompletionResult result = openAiService.createCompletion(request);
        List<String> choices = result.getChoices().stream().map(c -> c.getText().trim()).toList();

        return choices.isEmpty() ? "No response from OpenAI" : choices.get(0);
    }

    public List<CreditCard> executeQuery(String naturalLanguageQuery) {
        try {
            String mongoQuery = generateMongoQuery(naturalLanguageQuery);
            return cardRepository.findByCustomQuery(mongoQuery).stream().map(creditCardMapper::toDto).toList();
        } catch (Exception e) {
            log.error("Error executing query: {}", e.getMessage());
            return cardRepository.findAll().stream().map(creditCardMapper::toDto).toList();
        }
    }
}