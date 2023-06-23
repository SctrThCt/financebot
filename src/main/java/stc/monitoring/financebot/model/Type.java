package stc.monitoring.financebot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Type {
    TYPE_INCOME("Доход"),
    TYPE_TRANSFER("Перевод между счетами"),
    TYPE_OUTCOME("Расход");


    private final String desc;

    @Override
    public String toString() {
        return name().substring(5);
    }
}
