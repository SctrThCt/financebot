package stc.monitoring.financebot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    CATEGORY_INCOME_SALARY("Зарплата"),
    CATEGORY_INCOME_GIFT("Внешний перевод"),
    CATEGORY_INCOME_OTHER("Прочее"),
    CATEGORY_OUTCOME_GROCERIES("Продукты"),
    CATEGORY_OUTCOME_TRAVEL("Путешествия"),
    CATEGORY_OUTCOME_RENT("Аренда"),
    CATEGORY_OUTCOME_MOVING("Перемещения"),
    CATEGORY_OUTCOME_RESTAURANTS("Кафе, рестораны"),
    CATEGORY_OUTCOME_HEALTH("Лекарства");

    private final String desc;
}
