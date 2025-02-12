package testproject.currencyrateservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "currency_rates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyRate {

    @Id
    private Long id;

    @Column(value = "currency")
    private String currency;

    @Column(value = "rate")
    private BigDecimal rate;

    @Column(value = "type")
    private CurrencyType type;

    @Column(value = "last_update")
    private LocalDateTime lastUpdate;

}
