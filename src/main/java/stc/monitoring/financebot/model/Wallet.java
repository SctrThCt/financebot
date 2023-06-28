package stc.monitoring.financebot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet extends BaseEntity{

    @OneToMany(fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private WalletType type;
}
