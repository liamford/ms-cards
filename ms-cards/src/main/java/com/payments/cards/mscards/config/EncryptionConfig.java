package com.payments.cards.mscards.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class EncryptionConfig {
    @Value("${encrypt.key}")
    private String encryptionKey;

    @Value("${encrypt.salt}")
    private String encryptionSalt;


    @Bean
    public TextEncryptor textEncryptor() {
        // Use the symmetric key and a hardcoded salt (replace with a secure salt in production)
        return Encryptors.text(encryptionKey, encryptionSalt);
    }
}
