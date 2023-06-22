package stc.monitoring.financebot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    BUTTON_INCOME_SALARY("Зарплата"),
    BUTTON_INCOME_GIFT("Внешний перевод"),
    BUTTON_INCOME_OTHER("Прочее"),
    BUTTON_OUTCOME_GROCERIES("Продукты"),
    BUTTON_OUTCOME_TRAVEL("Путешествия"),
    BUTTON_OUTCOME_RENT("Аренда"),
    BUTTON_OUTCOME_MOVING("Перемещения"),
    BUTTON_OUTCOME_RESTAURANTS("Кафе, рестораны"),
    BUTTON_OUTCOME_HEALTH("Лекарства"),
    BUTTON_TRANSFER("Перевод между счетами");

    private final String desc;

    public String toString() {
        return name().substring(7);
    }
}
