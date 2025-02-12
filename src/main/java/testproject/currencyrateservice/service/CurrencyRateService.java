package testproject.currencyrateservice.service;

import reactor.core.publisher.Mono;
import testproject.currencyrateservice.dto.CurrencyRatesResponseDTO;

public interface CurrencyRateService {

    Mono<CurrencyRatesResponseDTO> getCurrencyRates();

}
