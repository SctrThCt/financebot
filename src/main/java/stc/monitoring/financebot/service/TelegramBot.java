package stc.monitoring.financebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import stc.monitoring.financebot.config.BotConfig;
import stc.monitoring.financebot.model.Transaction;
import stc.monitoring.financebot.model.WhiteListUser;
import stc.monitoring.financebot.repository.TransactionRepository;
import stc.monitoring.financebot.repository.WhiteListRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static stc.monitoring.financebot.service.Buttons.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    HashMap<Long, StringBuffer> messages = new HashMap<>();
    HashMap<Long, Transaction> transactions = new HashMap<>();
    HashMap<Long, Integer> amounts = new HashMap<>();

    Map<Long, Long> whiteList;

    private final WhiteListRepository whiteListRepository;
    private final TransactionRepository transactionRepository;
    private final BotConfig config;


    public TelegramBot(BotConfig config, WhiteListRepository whiteListRepository, TransactionRepository transactionRepository) {

        super(config.getToken());
        this.config = config;
        this.whiteListRepository = whiteListRepository;
        whiteList = whiteListRepository.
                findAll().stream().collect(Collectors.toMap(WhiteListUser::getTelegramId, WhiteListUser::getId));
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long userId = update.getMessage().getChat().getId();
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (text.equals("/start")) {
                start(chatId, update.getMessage().getChat());
            } else if (text.startsWith("/")) {
                switch (text) {
                    default:
                        sendMessage(new SendMessage(String.valueOf(chatId), "Команда не распознана"));
                }

            } else {
                switch (text) {
                    case "password":
                        whiteListRepository.save(new WhiteListUser(update.getMessage().getFrom().getId()));
                        whiteList = whiteListRepository.
                                findAll().stream().collect(Collectors.toMap(WhiteListUser::getTelegramId, WhiteListUser::getId));
                        sendMessage(new SendMessage(String.valueOf(chatId), "Регистрация пройдена"));
                        break;
                    default:

                        if (!isAuthorized(userId)) {
                            sendMessage(new SendMessage(String.valueOf(chatId), "Неавторизованный доступ"));
                            break;
                        }
                        try {
                            amounts.put(userId, Util.checkIfCorrectPositiveNumber(text));
                            messages.put(userId, new StringBuffer("Вы "));
                            Transaction t = new Transaction();
                            t.setAmount(Util.checkIfCorrectPositiveNumber(text));
                            transactions.put(userId,t);
                            startMoneyRecord(chatId);
                        } catch (NumberFormatException e) {
                            sendMessage(new SendMessage(String.valueOf(chatId), "Команда не распознана"));
                        }
                }
            }
        } else if (update.hasCallbackQuery()) {
            long userId = update.getCallbackQuery().getMessage().getChat().getId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            switch (update.getCallbackQuery().getData()) {
                case BUTTON_SPEND:
                    messages.get(userId).append("потратили ").append(amounts.get(userId)).append(" на ");
                    updateMessage("Выберите категорию", chatId, messageId, getSpendingCategories());
                    transactions.get(userId).setAmount(transactions.get(userId).getAmount()*-1);
                    break;
                case BUTTON_EARN:
                    messages.get(userId).append("заработали ").append(amounts.get(userId)).append(" с ");
                    updateMessage("Выберите категорию", chatId, messageId, getEarningCategories());
                    break;
                case BUTTON_GRC:
                case BUTTON_RST:
                case BUTTON_OTHR:
                case BUTTON_FOUND:
                case BUTTON_GIFT:
                case BUTTON_SLR:
                    messages.get(userId).append(update.getCallbackQuery().getData());
                    updateMessage(messages.get(userId).toString(), chatId, messageId, null);
                    break;
            }
        }
    }

    private void start(long chatId, Chat chat) {
        String response = String.format("Привет, %s! \n Для продолжения введи пароль", chat.getFirstName());
        sendMessage(new SendMessage(String.valueOf(chatId), response));
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn("Всё сломалось");
        }
    }

    private void startMoneyRecord(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Укажите тип");
        message.setReplyMarkup(getTransactionTypeButtons());
        sendMessage(message);
    }

    private void updateMessage(String text, long chatId, int messageId, @Nullable InlineKeyboardMarkup markup) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(text);
        if (markup != null) {
            message.setReplyMarkup(markup);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn("Всё сломалось");
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    private boolean isAuthorized(long userId) {
        return whiteList.containsKey(userId);
    }
    public void processUpdateMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn("Всё сломалось");
        }
    }
}
