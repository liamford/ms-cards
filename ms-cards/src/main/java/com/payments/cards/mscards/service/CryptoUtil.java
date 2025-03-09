package com.payments.cards.mscards.service;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {
    private final TextEncryptor textEncryptor;

    public CryptoUtil(TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }


    public String encrypt(String plainText) {
        return plainText != null ? textEncryptor.encrypt(plainText) : null;
    }

    public String decrypt(String encryptedText) {
        return encryptedText != null ? textEncryptor.decrypt(encryptedText) : null;
    }
}
