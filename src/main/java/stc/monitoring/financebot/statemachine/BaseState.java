package stc.monitoring.financebot.statemachine;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BaseState {
    SendMessage perform();
}
