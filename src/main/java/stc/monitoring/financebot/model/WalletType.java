package stc.monitoring.financebot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletType {
    WALLET_CASH("Наличные"),
    WALLET_ELECTRONIC("Безналичные");

    private final String desc;
}
