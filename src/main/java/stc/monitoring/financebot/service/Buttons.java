package stc.monitoring.financebot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Buttons {

    public static final String BUTTON_SPEND = "SPEND";
    public static final String BUTTON_EARN = "EARN";

    public static final String BUTTON_GRC = "GRC";
    public static final String BUTTON_RST = "RST";
    public static final String BUTTON_OTHR = "OTHR";
    public static final String BUTTON_SLR = "SLR";
    public static final String BUTTON_GIFT = "GIFT";
    public static final String BUTTON_FOUND = "FOUND";

    public static InlineKeyboardMarkup getTransactionTypeButtons() {
        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton earnButton = new InlineKeyboardButton();
        earnButton.setText("Доход");
        earnButton.setCallbackData(BUTTON_EARN);

        InlineKeyboardButton spentButton = new InlineKeyboardButton();
        spentButton.setText("Расход");
        spentButton.setCallbackData(BUTTON_SPEND);

        buttons.add(earnButton);
        buttons.add(spentButton);
        rows.add(buttons);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getSpendingCategories() {
        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Продукты");
        button1.setCallbackData(BUTTON_GRC);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Кафешечька");
        button2.setCallbackData(BUTTON_RST);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Прост))0)0");
        button3.setCallbackData(BUTTON_OTHR);

        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);

        rows.add(buttons);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getEarningCategories() {

        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Зарплата");
        button1.setCallbackData(BUTTON_SLR);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Перевели");
        button2.setCallbackData(BUTTON_GIFT);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Нашли");
        button3.setCallbackData(BUTTON_FOUND);

        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);

        rows.add(buttons);
        out.setKeyboard(rows);
        return out;
    }
}
