package ru.belov.testspringcargobot.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
public class MenuController {
    public static InlineKeyboardMarkup getMainMenu() {

        InlineKeyboardButton faqButton = InlineKeyboardButton.builder()
                .text("FAQ")
                .callbackData("/faq")
                .build();

        InlineKeyboardButton contactButton = InlineKeyboardButton.builder()
                .text("Связаться с нами")
                .callbackData("/contactus")
                .build();

        InlineKeyboardButton questionsButton = InlineKeyboardButton.builder()
                .text("Часто-задаваемые вопросы")
                .callbackData("/questions")
                .build();

        List<InlineKeyboardButton> keyboardButtonsFloorOne = List.of(faqButton, contactButton);
        List<InlineKeyboardButton> keyboardButtonsFloorTwo = List.of(questionsButton);

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(keyboardButtonsFloorOne, keyboardButtonsFloorTwo))
                .build();
    }
}
