package ru.belov.testspringcargobot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.belov.testspringcargobot.domain.MenuController;
import ru.belov.testspringcargobot.domain.Questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CargoInformationBot extends TelegramLongPollingBot {
    static final String CONTACT_TEXT = "src/main/resources/contactus.txt";
    static final String HELP_TEXT_FILE_PATH = "src/main/resources/help.txt";
    static final String DELIVERY_TEXT = "src/main/resources/delivery.txt";
    static final String ERROR_MESSAGE = "Sorry command is unavailable";
    private final BotConfig botConfig;

    private final String helpText;
    private final String faqText;
    private final String deliveryText;

    public CargoInformationBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        this.helpText = EmojiParser.parseToUnicode(readTextFromFile(HELP_TEXT_FILE_PATH));
        this.faqText = EmojiParser.parseToUnicode(readTextFromFile(CONTACT_TEXT));
        this.deliveryText = EmojiParser.parseToUnicode(readTextFromFile(DELIVERY_TEXT));

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Запустить бота"));

        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private String readTextFromFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line;
            while ((line = reader.readLine()) != null){
                content.append(line).append("\n");
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
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
                    message.setText(helpText);
                    message.setReplyMarkup(createBackButton());
                    message.setParseMode("HTML");
                    break;
                case "/contactus":
                    message.setText(faqText);
                    message.setReplyMarkup(createBackButton());
                    break;
                case "/questions":
                    message.setText("Выберите тему вопросов");
                    message.setReplyMarkup(Questions.questionsMenu());
                    break;
                case "/payment":
                    message.setText("Оплата производится наличными");
                    message.setReplyMarkup(Questions.questionsBackButton(callBackData));
                    break;
                case "/delivery":
                    message.setText(deliveryText);
                    message.setReplyMarkup(Questions.questionsBackButton(callBackData));
                    message.setParseMode("HTML");
                    break;
                case "/back" :
                    message.setText("Выберите пункт");
                    message.setReplyMarkup(MenuController.getMainMenu());
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
        SendMessage sendMessage = fillMessageAndChatId(chatId, ERROR_MESSAGE);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendBothMessages(long chatId, String textToSend) {
        InlineKeyboardMarkup mainMenu = MenuController.getMainMenu();
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
