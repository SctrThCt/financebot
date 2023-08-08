package stc.monitoring.financebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stc.monitoring.financebot.model.Rate;
@Repository
public interface RateRepository extends JpaRepository<Rate,Long> {
}
