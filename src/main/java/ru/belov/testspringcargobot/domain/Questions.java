package ru.belov.testspringcargobot.domain;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Questions implements Serializable {

    public static InlineKeyboardMarkup questionsMenu() {

        InlineKeyboardButton delivery = InlineKeyboardButton.builder().text("Доставка").callbackData("/delivery").build();
        InlineKeyboardButton payment = InlineKeyboardButton.builder().text("Оплата").callbackData("/payment").build();
        InlineKeyboardButton orders = InlineKeyboardButton.builder().text("Заказы").callbackData("/orders").build();
        InlineKeyboardButton back = InlineKeyboardButton.builder().text("Назад").callbackData("/back").build();

        List<InlineKeyboardButton> keyboardButtonsFloorOne = Arrays.asList(orders, payment, delivery);
        List<InlineKeyboardButton> keyboardButtonsFloorTwo = List.of(back);

        return new InlineKeyboardMarkup(List.of(keyboardButtonsFloorOne, keyboardButtonsFloorTwo));
    }

    public static InlineKeyboardMarkup questionsBackButton(String callBackData){
        InlineKeyboardMarkup buttons = new InlineKeyboardMarkup();

        InlineKeyboardButton delivery = InlineKeyboardButton.builder().text("Доставка").callbackData("/delivery").build();
        InlineKeyboardButton payment = InlineKeyboardButton.builder().text("Оплата").callbackData("/payment").build();
        InlineKeyboardButton orders = InlineKeyboardButton.builder().text("Заказы").callbackData("/orders").build();
        InlineKeyboardButton back = InlineKeyboardButton.builder().text("Назад").callbackData("/questions").build();

        List<InlineKeyboardButton> keyboardButtonsFloorOne = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsFloorTwo = List.of(back);

        switch (callBackData){
            case "/payment" : keyboardButtonsFloorOne = Arrays.asList(orders, delivery);
                break;
            case "/orders" : keyboardButtonsFloorOne = Arrays.asList(payment, delivery);
                break;
            case "/delivery" : keyboardButtonsFloorOne = Arrays.asList(orders, payment);
                break;
        }
        buttons.setKeyboard(List.of(keyboardButtonsFloorOne, keyboardButtonsFloorTwo));
        return buttons;
    }
}
