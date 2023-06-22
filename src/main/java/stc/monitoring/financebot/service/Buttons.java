package stc.monitoring.financebot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import stc.monitoring.financebot.model.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Category> categories = Arrays.stream(Category.values())
                .filter(e->e.toString().startsWith("OUTCOME"))
                .collect(Collectors.toList());

        for(int i = 0; i < categories.size();i++)
        {
            if (i%3==0)
            {
                rows.add(buttons);
                buttons = new ArrayList<>();
            }
                InlineKeyboardButton b = new InlineKeyboardButton();
                b.setText(categories.get(i).getDesc());
                b.setCallbackData(categories.get(i).toString());
                buttons.add(b);
        }
        rows.add(buttons);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getEarningCategories() {

        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Category c:Category.values()) {
            if (c.toString().startsWith("INCOME")) {
                InlineKeyboardButton b = new InlineKeyboardButton();
                b.setText(c.getDesc());
                b.setCallbackData(c.toString());
                buttons.add(b);
            }
        }

        rows.add(buttons);
        out.setKeyboard(rows);
        return out;
    }
}
