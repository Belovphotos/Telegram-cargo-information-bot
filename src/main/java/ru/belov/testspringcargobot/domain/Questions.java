package ru.belov.testspringcargobot.domain;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Questions {

    public static InlineKeyboardMarkup questionsMenu() {

        InlineKeyboardButton delivery = InlineKeyboardButton.builder().text("Доставка").callbackData("/delivery").build();
        InlineKeyboardButton payment = InlineKeyboardButton.builder().text("Оплата").callbackData("/payment").build();
        InlineKeyboardButton orders = InlineKeyboardButton.builder().text("Заказы").callbackData("/orders").build();

        List<InlineKeyboardButton> keyboardButtonsFloorOne = Arrays.asList(orders, payment, delivery);

        return new InlineKeyboardMarkup(List.of(keyboardButtonsFloorOne));
    }
}
