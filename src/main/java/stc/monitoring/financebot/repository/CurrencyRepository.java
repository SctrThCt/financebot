package stc.monitoring.financebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stc.monitoring.financebot.model.Currency;

import java.util.List;
import java.util.Optional;
@Repository
public interface CurrencyRepository extends JpaRepository<Currency,Long> {

    List<Currency> getAllByInUseTrue();
    List<Currency> getAllByInUseFalse();

    Currency findCurrencyByCode(String code);
}
