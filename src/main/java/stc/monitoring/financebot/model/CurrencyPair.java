package stc.monitoring.financebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Table(name = "currency_pair")
@Entity
@Getter
@Setter
public class CurrencyPair extends BaseEntity{

    @JoinColumn(name = "currency1")
    @ManyToOne
    private Currency currency1;

    @JoinColumn(name = "currency2")
    @ManyToOne
    private Currency currency2;

    @Column(name = "rate")
    private float rate;

    @Column(name = "request_date")
    private Date requestDate;

}
