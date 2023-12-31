package stc.monitoring.financebot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import stc.monitoring.financebot.model.Category;
import stc.monitoring.financebot.model.Currency;
import stc.monitoring.financebot.model.Type;
import stc.monitoring.financebot.model.Wallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyboardConstructor {
    public static final String BUTTON_CANCEL = "CANCEL";

    public static InlineKeyboardMarkup getTransactionTypeButtons() {
        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Type t : Type.values()) {
            InlineKeyboardButton b = new InlineKeyboardButton();
            b.setText(t.getDesc());
            b.setCallbackData(t.toString());
            buttons.add(b);
        }
        rows.add(buttons);
        addCancelButton(rows);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getSpendingCategories() {
        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        List<Category> categories = Arrays.stream(Category.values())
                .filter(e -> e.toString().contains("OUTCOME"))
                .collect(Collectors.toList());

        for (int i = 0; i < categories.size(); i++) {
            if (i % 3 == 0) {
                rows.add(buttons);
                buttons = new ArrayList<>();
            }
            InlineKeyboardButton b = new InlineKeyboardButton();
            b.setText(categories.get(i).getDesc());
            b.setCallbackData(categories.get(i).toString());
            buttons.add(b);
        }
        rows.add(buttons);
        addCancelButton(rows);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getEarningCategories() {

        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Category c : Category.values()) {
            if (c.toString().contains("INCOME")) {
                InlineKeyboardButton b = new InlineKeyboardButton();
                b.setText(c.getDesc());
                b.setCallbackData(c.toString());
                buttons.add(b);
            }
        }

        rows.add(buttons);
        addCancelButton(rows);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getCurrencies(List<Currency> currencies) {
        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (int i = 0; i < currencies.size(); i++) {
            if (i % 3 == 0) {
                rows.add(buttons);
                buttons = new ArrayList<>();
            }
            InlineKeyboardButton b = new InlineKeyboardButton();
            b.setText(currencies.get(i).getName());
            b.setCallbackData("CURRENCY_" + currencies.get(i).getCode());
            buttons.add(b);
        }

        rows.add(buttons);
        addCancelButton(rows);
        out.setKeyboard(rows);
        return out;
    }

    public static InlineKeyboardMarkup getWalletsForCurrency(List<Wallet> wallets) {
        InlineKeyboardMarkup out = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Wallet w : wallets) {
            InlineKeyboardButton b = new InlineKeyboardButton();
            b.setText(w.getType().getDesc());
            b.setCallbackData(w.getType().toString());
            buttons.add(b);
        }

        rows.add(buttons);
        addCancelButton(rows);
        out.setKeyboard(rows);
        return out;
    }

    private static void addCancelButton(List<List<InlineKeyboardButton>> rows) {
        List<InlineKeyboardButton> button = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setCallbackData(BUTTON_CANCEL);
        cancelButton.setText("Отмена");
        button.add(cancelButton);
        rows.add(button);
    }

}
