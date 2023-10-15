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
import ru.belov.testspringcargobot.domain.Questions;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CargoInformationBot extends TelegramLongPollingBot {


    static final String HELP_TEXT = EmojiParser.parseToUnicode("Мы - быстро растущая компания доставки товаров из Китая!\n\n" +
            ":cn:");
    static final String CONTACT_TEXT = "Наш менеджер по работе с клиентами ответит " +
            "на все интересущие Вас вопросы! t.me/peachbaker";
    static final String ERROR_MESSAGE = "Sorry command is unavailable";

    private final BotConfig botConfig;

    public CargoInformationBot(BotConfig botConfig) {
        this.botConfig = botConfig;

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Запустить бота"));

        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
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
                    message.setText(HELP_TEXT);
                    message.setReplyMarkup(createBackButton());
                    break;
                case "/contactus":
                    message.setText(CONTACT_TEXT);
                    message.setReplyMarkup(createBackButton());
                    break;
                case "/questions":
                    message.setText("Выберите тему");
                    message.setReplyMarkup(Questions.questionsMenu());
                    break;
                default:
                    message.setText("Выберите пункт");
                    message.setReplyMarkup(getMainMenu());
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
        InlineKeyboardMarkup mainMenu = getMainMenu();
        SendMessage sendMessage = fillMessageAndChatId(chatId, textToSend);
        sendMessage.setReplyMarkup(mainMenu);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private InlineKeyboardMarkup getMainMenu() {

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsFloorOne = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsFloorTwo = new ArrayList<>();

        InlineKeyboardButton faqButton = new InlineKeyboardButton();
        faqButton.setText("FAQ");
        faqButton.setCallbackData("/faq");

        InlineKeyboardButton contactButton = new InlineKeyboardButton();
        contactButton.setText("Связаться с нами");
        contactButton.setCallbackData("/contactus");

        InlineKeyboardButton questionsButton = new InlineKeyboardButton();
        questionsButton.setText("Часто-задаваемые вопросы");
        questionsButton.setCallbackData("/questions");

        keyboardButtonsFloorOne.add(faqButton);
        keyboardButtonsFloorOne.add(contactButton);

        keyboardButtonsFloorTwo.add(questionsButton);

        buttons.add(keyboardButtonsFloorOne);
        buttons.add(keyboardButtonsFloorTwo);

        return new InlineKeyboardMarkup(buttons);
    }

    private SendMessage fillMessageAndChatId(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        return sendMessage;
    }

    private InlineKeyboardMarkup createBackButton() {

        InlineKeyboardMarkup backKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> firstFloorButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("/back");

        keyboardButtons.add(backButton);

        firstFloorButtons.add(keyboardButtons);
        backKeyBoard.setKeyboard(firstFloorButtons);
        return backKeyBoard;
    }
}
