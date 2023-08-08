package stc.monitoring.financebot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Table(name = "rates")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rate extends BaseEntity {

    @JoinColumn(name = "currency_id")
    @ManyToOne
    private Currency currency;

    @Column(name = "rate")
    private double rate;

    @Column(name = "request_date")
    private LocalDate requestDate;

}
