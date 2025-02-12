package testproject.currencyrateservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import testproject.currencyrateservice.dto.CurrencyRatesResponseDTO;
import testproject.currencyrateservice.service.CurrencyRateServiceImpl;

@RestController
@RequestMapping("/currency-rates")
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateController {

    private final CurrencyRateServiceImpl currencyRateServiceImpl;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<CurrencyRatesResponseDTO> getCurrencyRates() {
        return currencyRateServiceImpl.getCurrencyRates();
    }

}
