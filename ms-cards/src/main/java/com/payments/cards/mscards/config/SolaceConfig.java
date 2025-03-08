package com.payments.cards.mscards.config;

import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SolaceConfig {

    @Value("${solace.java.host}")
    private String solaceHost;

    @Value("${solace.java.msgVpn}")
    private String solaceVpn;

    @Value("${solace.java.clientUsername}")
    private String solaceUsername;

    @Value("${solace.java.clientPassword}")
    private String solacePassword;

    @Bean
    public JCSMPSession jcsmpSession() throws Exception {
        JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, solaceHost);
        properties.setProperty(JCSMPProperties.VPN_NAME, solaceVpn);
        properties.setProperty(JCSMPProperties.USERNAME, solaceUsername);
        properties.setProperty(JCSMPProperties.PASSWORD, solacePassword);
        return JCSMPFactory.onlyInstance().createSession(properties);
    }


}
