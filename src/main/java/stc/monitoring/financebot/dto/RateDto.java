package stc.monitoring.financebot.dto;

import java.time.LocalDate;

public record RateDto(String currency, Double rate, LocalDate date) {
}
