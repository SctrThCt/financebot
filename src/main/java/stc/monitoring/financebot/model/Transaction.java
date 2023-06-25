package stc.monitoring.financebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Table(name = "transactions")
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Transaction extends BaseEntity {

    @Column(name = "amount")
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "localDate", columnDefinition = "timestamp default now()",nullable = false)
    private Date created = new Date();
}
