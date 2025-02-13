package testproject.currencyrateservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testproject.currencyrateservice.dto.CryptoCurrencyRateDTO;
import testproject.currencyrateservice.dto.CurrencyRatesResponseDTO;
import testproject.currencyrateservice.dto.FiatCurrencyRateDTO;
import testproject.currencyrateservice.model.CurrencyRate;
import testproject.currencyrateservice.model.CurrencyType;
import testproject.currencyrateservice.repository.CurrencyRateRepository;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceImplTest {

    private static final String CURRENCY_CODE_USD = "USD";
    private static final String CURRENCY_CODE_BTC = "BTC";

    @Mock
    private CurrencyMockApiService currencyMockApiService;

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyRateServiceImpl currencyRateService;

    @Test
    void testGetCurrencyRates_FiatAndCryptoRates() {
        FiatCurrencyRateDTO fiatRateDTO = new FiatCurrencyRateDTO(CURRENCY_CODE_USD, BigDecimal.valueOf(1.2));
        CryptoCurrencyRateDTO cryptoRateDTO = new CryptoCurrencyRateDTO(CURRENCY_CODE_BTC, BigDecimal.valueOf(35000));

        when(currencyMockApiService.fetchFiatRates()).thenReturn(Flux.just(fiatRateDTO));
        when(currencyMockApiService.fetchCryptoRates()).thenReturn(Flux.just(cryptoRateDTO));

        when(currencyRateRepository.save(any(CurrencyRate.class)))
                .thenReturn(Mono.just(new CurrencyRate()));

        when(currencyRateRepository.findByCurrencyAndType(eq(CURRENCY_CODE_USD), eq(CurrencyType.FIAT)))
                .thenReturn(Mono.empty());
        when(currencyRateRepository.findByCurrencyAndType(eq(CURRENCY_CODE_BTC), eq(CurrencyType.CRYPTO)))
                .thenReturn(Mono.empty());

        when(currencyRateRepository.findAllByType(CurrencyType.FIAT))
                .thenReturn(Flux.empty());
        when(currencyRateRepository.findAllByType(CurrencyType.CRYPTO))
                .thenReturn(Flux.empty());

        Mono<CurrencyRatesResponseDTO> response = currencyRateService.getCurrencyRates();

        StepVerifier.create(response)
                .expectNextMatches(dto -> dto.getFiat().size() == 1
                        && dto.getCrypto().size() == 1)
                .verifyComplete();

        verify(currencyRateRepository, times(2)).save(any(CurrencyRate.class));
    }

}
