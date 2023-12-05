package stc.monitoring.financebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stc.monitoring.financebot.model.Wallet;
import stc.monitoring.financebot.model.WalletType;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findWalletByCurrencyIdAndType(long currencyId, WalletType type);

    List<Wallet> findAllByCurrencyId(long currencyId);
}
