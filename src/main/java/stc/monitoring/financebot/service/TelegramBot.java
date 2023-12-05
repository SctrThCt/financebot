package stc.monitoring.financebot.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
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
import stc.monitoring.financebot.model.*;
import stc.monitoring.financebot.repository.CurrencyRepository;
import stc.monitoring.financebot.repository.TransactionRepository;
import stc.monitoring.financebot.repository.WalletRepository;
import stc.monitoring.financebot.repository.WhiteListRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static stc.monitoring.financebot.service.KeyboardConstructor.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private HashMap<Long, Transaction> transactions = new HashMap<>();
    private HashMap<Long, Integer> amounts = new HashMap<>();
    private Map<Long, Long> whiteList;

    private final WhiteListRepository whiteListRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyRepository currencyRepository;
    private final WalletRepository walletRepository;
    private final RateService rateService;
    private final BotConfig config;


    public TelegramBot(BotConfig config, WhiteListRepository whiteListRepository, TransactionRepository transactionRepository, CurrencyRepository currencyRepository, WalletRepository walletRepository, RateService rateService) {

        super(config.getToken());
        this.config = config;
        this.whiteListRepository = whiteListRepository;
        whiteList = whiteListRepository.
                findAll().stream().collect(Collectors.toMap(WhiteListUser::getTelegramId, WhiteListUser::getId));
        this.transactionRepository = transactionRepository;
        this.currencyRepository = currencyRepository;
        this.walletRepository = walletRepository;
        this.rateService = rateService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long userId = update.getMessage().getChat().getId();
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (text.equals("/start")) {
                start(chatId, update.getMessage().getChat());
            } else if (text.equals("/currencies")) {
                currencies(chatId);
            } else if (text.equals("/rates")) {
                rates(chatId);
            } else if (text.equals("/amo-access")) {
                getAccessAmoToken(chatId);
            } else if (text.startsWith("/")) {
                switch (text) {
                    default:
                        sendMessage(new SendMessage(String.valueOf(chatId), "Команда не распознана"));
                }

            } else {
                switch (text) {
                    case "password":
                        addUserToWhitelist(update);
                        break;
                    default:
                        if (!isAuthorized(userId)) {
                            sendMessage(new SendMessage(String.valueOf(chatId), "Неавторизованный доступ"));
                            break;
                        } else {
                            try {
                                amounts.put(userId, Util.checkIfCorrectPositiveNumber(text));
                                Transaction t = new Transaction();
                                t.setAmount(Util.checkIfCorrectPositiveNumber(text));
                                transactions.put(userId, t);
                                startMoneyRecord(chatId);
                            } catch (NumberFormatException e) {
                                sendMessage(new SendMessage(String.valueOf(chatId), "Команда не распознана"));
                            }
                        }
                }
            }
        } else if (update.hasCallbackQuery()) {

            long userId = update.getCallbackQuery().getMessage().getChat().getId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            String callbackData = update.getCallbackQuery().getData();
            if (!isAuthorized(userId)) {
                sendMessage(new SendMessage(String.valueOf(chatId), "Неавторизованный доступ"));
            } else {
                switch (callbackData.split("_")[0]) {

                    case "TYPE":
                        handleType(callbackData, transactions.get(userId), chatId, messageId);
                        break;
                    case "CATEGORY":
                        handleCategory(callbackData, transactions.get(userId), chatId, messageId);
                        break;
                    case "CURRENCY":
                        handleCurrency(callbackData, transactions.get(userId), chatId, messageId);
                        break;
                    case "WALLET":
                        handleWalletType(callbackData, transactions.get(userId), chatId, messageId);
                        break;
                    case "CANCEL":


                }
            }
        }
    }

    //ХЗ нужно ли вообще это потому что им пользуемся только мы, но на будущее мб пригодится
    private void start(long chatId, Chat chat) {
        String response = String.format("Привет, %s!\nДля продолжения введи пароль", chat.getFirstName());
        sendMessage(new SendMessage(String.valueOf(chatId), response));
    }

    //Отправить сообщение от бота
    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn("Всё сломалось");
        }
    }


    //Начало записи от транзакции (хз, мб добавить какое то подобие транзакционности к БД)
    private void startMoneyRecord(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Укажите тип");
        message.setReplyMarkup(getTransactionTypeButtons());
        sendMessage(message);
    }

    //Обновить сообщение от бота (чтобы по десять раз не писать новое сообщение)
    private void updateMessage(String text, long chatId, long messageId, @Nullable InlineKeyboardMarkup markup) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId((int) messageId);
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

    //Вывести список валют
    private void currencies(long chatId) {
        StringBuilder response = new StringBuilder();
        currencyRepository.getAllByInUseTrue().forEach(c -> response.append(c.getName() + "\n"));
        sendMessage(new SendMessage(String.valueOf(chatId), "Выбранные валюты: \n" + response.toString()));
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }


    //Проверка на нахождение юзера в вайтлисте
    private boolean isAuthorized(long userId) {
        return whiteList.containsKey(userId);
    }

    //добавить юзера в вайтлист если он ввел пароль (при повторном введении пароля в БД запись дублируется, исправить)
    private void addUserToWhitelist(Update update) {
        whiteListRepository.save(new WhiteListUser(update.getMessage().getFrom().getId()));
        whiteList = whiteListRepository.
                findAll().stream().collect(Collectors.toMap(WhiteListUser::getTelegramId, WhiteListUser::getId));
        sendMessage(new SendMessage(String.valueOf(update.getMessage().getChatId()), "Регистрация пройдена"));
    }

    //Указание типа транзакции при добавлении новой
    private void handleType(String data, Transaction transaction, long chatId, long messageId) {
        Type type = Type.valueOf(data);
        switch (type) {
            case TYPE_OUTCOME:
                updateMessage("Выберите категорию", chatId, messageId, getSpendingCategories());
                break;
            case TYPE_INCOME:
                updateMessage("Выберите категорию", chatId, messageId, getEarningCategories());
                break;
        }
        transaction.setType(type);
    }

    //Указание категории трат при добавлении новой транзакции
    private void handleCategory(String data, Transaction transaction, long chatId, int messageId) {
        Category category = Category.valueOf((data));
        transaction.setCategory(category);
        List<Currency> currencies = currencyRepository.getAllByInUseTrue();
        updateMessage("Укажите валюту", chatId, messageId, getCurrencies(currencies));
    }

    //Обновление курсов (перенести в другой класс)
    void rates(long chatId) {
        StringBuilder response = new StringBuilder("Обновленные курсы валют на ");
        List<Rate> rateList = rateService.getCurrencyRates();
        response.append(rateList.get(0).getRequestDate()).append(":\n");
        rateList.forEach(rate ->
                response.append(rate.getCurrency().getName())
                        .append(": ")
                        .append(rate.getRate())
                        .append("\n"));

        sendMessage(new SendMessage(String.valueOf(chatId), response.toString()));
    }

    void addWallet(Currency currency, WalletType type) {
        Wallet wallet = new Wallet();
        wallet.setCurrency(currency);
        wallet.setType(type);
        walletRepository.save(wallet);
    }

    void handleCurrency(String data, Transaction transaction, long chatId, int messageId) {
        Currency currency = currencyRepository.findCurrencyByCode(data.split("_")[1]);
        transaction.setCurrency(currency);
        updateMessage("Укажите тип валюты", chatId, messageId, getWalletsForCurrency(walletRepository.findAllByCurrencyId(transaction.getCurrency().getId())));
    }

    void handleWalletType(String data, Transaction transaction, long chatId, int messageId) {
        Wallet wallet = walletRepository.findWalletByCurrencyIdAndType(transaction.getCurrency().getId(),
                WalletType.valueOf(data));
        transaction.setWallet(wallet);
        transactionRepository.save(transaction);
        updateMessage("Транзакция сохранена", chatId, messageId, null);
    }

    void checkIfAuthorized(long userId, long chatId) {
        if (!isAuthorized(userId)) {
            sendMessage(new SendMessage(String.valueOf(chatId), "Неавторизованный доступ"));
        }
    }

    void getAccessAmoToken(long chatId) {
        @AllArgsConstructor
        @JsonDeserialize
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        class AmoRequest {
            String client_id;
            String client_secret;
            String grant_type;
            String code;
            String redirect_uri;
        }

        String CLIENT_ID = "51e46ba7-aeb9-43cc-b119-de8256cfce15";
        String SECRET = "7Tz34K2cwAUZDuB4CycKCSycHbWo70YWuj14zGUS9B6dyh54hGYabhIlhQVAoGxw";
        String GRANT_TYPE = "authorization_code";
        String CODE = "def50200447c3b70efc2885c3bbf0c119861656ab0ec750c56f4fb5c402a4a3260f43d897c95cefe15fb0c40b383f282a7544165d9c542b5207440deb6d69e494cf4e47d86917e6a0f74dfc3ed1eb512f208b383efaf64edead92ba3eea36b11f01aeef119cdb5165615dcc9f4ae669f1e5b9b3a787270d78c060478cfa6adad9a73ee5a0150f2de56ad7dfa978e22d6b3bf1fc0e9bd48d1ac9af52becb95df3bd7639db3c2150c71834ce66cde3097a9ec14636dca8ed0cdc3a0a2551e121cd1aa76af99d3e436701b6c6fbcfa1baefc604075452dd010f77af248daefe4ba6b479671b2f3f8c89d39994cf71aae409cd0f1414d054f40966bb77809e5f6837221bbaecb7929797c2a57e0de7f4aeef9dde72c5185c9be8b2aa870be45644e5aa4af27cff4180907b2f2f3a1aa08e9c74f7e62491d34941813d3e9da406d0016ffaa52164d9f8febb2667268a7d062a2e595a7588dd0b8cf78f52cb110956cf8d7783511925ab18a9df43b016c21da054c2b9ac0effc96a0334d3f4b7adef716cfa0bed6484aeedd481dc56e9aa3597bab92ce1a7da264dd9237c1fa60389a8e00236da8329afd46591858755a32d7831ad85c6dd2b9c90e4fb2b688ece825ddb87419207ff458634eb92939c8f9ea34ac8dbaaa35c79e9662c8dcaa3d33d8500c61a94768b4d4b79ae10f557c79dcb658b3947c4";
        String BASE_URL = "https://projectpulseonline.amocrm.ru";

        HttpClient client = HttpClient.newHttpClient();

        ObjectMapper mapper = new ObjectMapper();
        String payload = new String();
        try {
            payload = mapper.writeValueAsString(new AmoRequest(
                    CLIENT_ID,
                    SECRET,
                    GRANT_TYPE,
                    CODE,
                    BASE_URL));
            sendMessage(new SendMessage(String.valueOf(chatId), payload.replace(",",",\n")));
        } catch (Exception e) {
            sendMessage(new SendMessage(String.valueOf(chatId), e.toString()));
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/oauth2/access_token"))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request,HttpResponse.BodyHandlers.ofString());
            StringBuilder tgResponse = new StringBuilder();
            tgResponse.append(response.statusCode()+"\n");
            response.headers().map().forEach((k,v)->tgResponse.append(k+":"+v+"\n"));
            tgResponse.append(response.body());
            sendMessage(new SendMessage(String.valueOf(chatId),tgResponse.toString()));
        }  catch (Exception e) {
            sendMessage(new SendMessage(String.valueOf(chatId),"Биба"));
        }
     }

}
