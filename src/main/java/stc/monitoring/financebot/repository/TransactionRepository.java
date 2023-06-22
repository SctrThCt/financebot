package stc.monitoring.financebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stc.monitoring.financebot.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
