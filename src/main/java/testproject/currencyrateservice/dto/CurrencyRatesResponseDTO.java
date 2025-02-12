package testproject.currencyrateservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRatesResponseDTO {

    private List<CurrencyRate> fiat;
    private List<CurrencyRate> crypto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrencyRate {
        private String currency;
        private BigDecimal rate;
    }

}
