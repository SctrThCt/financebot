package stc.monitoring.financebot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Table(name = "transactions")
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Transaction extends BaseEntity {
    @Column(name = "amount")
    private long amount;
    @Column(name = "category")
    private Category category;
    @Column(name = "source")
    private String source;
    @Column(name = "localDate")
    private LocalDate localDate;
}
