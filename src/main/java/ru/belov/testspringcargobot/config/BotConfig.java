package ru.belov.testspringcargobot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    @Value("${faq.text.path}")
    String faqTextPath;

    @Value("${contact.text.path}")
    String contactTextPath;

    @Value("${delivery.text.path}")
    String deliveryTextPath;

    @Value("${orders.text.path}")
    String ordersTextPath;

    @Value("${payment.text.path}")
    String paymentTextPath;
}
