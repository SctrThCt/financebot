package stc.monitoring.financebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stc.monitoring.financebot.model.Currency;
import stc.monitoring.financebot.model.Wallet;
import stc.monitoring.financebot.model.WalletType;
import stc.monitoring.financebot.repository.WalletRepository;

@Component
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet addNewWallet(Currency currency, WalletType type) {
        return new Wallet();
    }
}
