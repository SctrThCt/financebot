package stc.monitoring.financebot.statemachine;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import stc.monitoring.financebot.model.Transaction;

import static stc.monitoring.financebot.service.KeyboardConstructor.getTransactionTypeButtons;

@RequiredArgsConstructor
public class StateStartMoneyRecord implements StatePerformingTransaction {

    private final Update update;

    @Override
    public SendMessage perform() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Укажите тип");
        sendMessage.setReplyMarkup(getTransactionTypeButtons());
        return sendMessage;
    }
}
