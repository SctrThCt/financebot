package stc.monitoring.financebot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "whitelist")
@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class WhiteListUser extends BaseEntity{
    @Column(name = "telegram_id")
    private long telegramId;
}
