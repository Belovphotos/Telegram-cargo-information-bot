package ru.belov.testspringcargobot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.belov.testspringcargobot.config.BotConfig;
import ru.belov.testspringcargobot.controller.MenuController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CargoInformationBot extends TelegramLongPollingBot {

    private final MenuController menuController;
    private final BotConfig botConfig;


    public CargoInformationBot(BotConfig botConfig, MenuController menuController) {
        this.botConfig = botConfig;
        this.menuController = menuController;

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Запустить бота"));

        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Failed to set bot commands", e);
        }

    }

    private String readTextFromFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ioe) {
            log.error("Failed to read text from file: " + path, ioe);
        }
        return content.toString();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                sendHelloMessage(chatId, update.getMessage().getChat().getFirstName());
            } else {
                sendErrorMessage(chatId);
            }
        } else if (update.hasCallbackQuery()) {

            String callBackData = update.getCallbackQuery().getData();

            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            EditMessageText message = new EditMessageText();

            switch (callBackData) {
                case "/faq":
                    message.setText(EmojiParser.parseToUnicode(readTextFromFile(botConfig.getFaqTextPath())));
                    message.setReplyMarkup(createBackButton());
                    message.setParseMode("HTML");
                    break;
                case "/contactus":
                    message.setText(EmojiParser.parseToUnicode(readTextFromFile(botConfig.getContactTextPath())));
                    message.setReplyMarkup(createBackButton());
                    break;
                case "/questions":
                    message.setText("Выберите тему вопросов");
                    message.setReplyMarkup(menuController.questionsMenu());
                    break;
                case "/orders":
                    message.setText(EmojiParser.parseToUnicode(readTextFromFile(botConfig.getOrdersTextPath())));
                    message.setReplyMarkup(menuController.questionsBackButton(callBackData));
                    message.setParseMode("HTML");
                    break;
                case "/payment":
                    message.setText(EmojiParser.parseToUnicode(readTextFromFile(botConfig.getPaymentTextPath())));
                    message.setReplyMarkup(menuController.questionsBackButton(callBackData));
                    message.setParseMode("HTML");
                    break;
                case "/delivery":
                    message.setText(EmojiParser.parseToUnicode(readTextFromFile(botConfig.getDeliveryTextPath())));
                    message.setReplyMarkup(menuController.questionsBackButton(callBackData));
                    message.setParseMode("HTML");
                    break;
                case "/back" :
                    message.setText("Выберите пункт");
                    message.setReplyMarkup(menuController.getMainMenu());
                    break;
            }
            message.setChatId(String.valueOf(chatId));
            message.setMessageId((int) messageId);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
    private void sendHelloMessage(long chatId, String name) {
        String response = "Hi, " + name + "! Nice to meet you!";
        sendBothMessages(chatId, response);
    }
    private void sendErrorMessage(long chatId) {
        SendMessage sendMessage = fillMessageAndChatId(chatId, "Sorry, wrong command");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendBothMessages(long chatId, String textToSend) {
        InlineKeyboardMarkup mainMenu = menuController.getMainMenu();
        SendMessage sendMessage = fillMessageAndChatId(chatId, textToSend);
        sendMessage.setReplyMarkup(mainMenu);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage fillMessageAndChatId(long chatId, String text) {
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
    }
    private InlineKeyboardMarkup createBackButton() {

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData("/back")
                .build();

        List<InlineKeyboardButton> keyboardButtons = List.of(backButton);
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(keyboardButtons))
                .build();

    }
}
