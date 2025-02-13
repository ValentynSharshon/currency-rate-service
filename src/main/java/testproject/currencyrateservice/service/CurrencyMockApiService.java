package testproject.currencyrateservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testproject.currencyrateservice.dto.CryptoCurrencyRateDTO;
import testproject.currencyrateservice.dto.FiatCurrencyRateDTO;
import testproject.currencyrateservice.exception.ServerConnectionException;
import testproject.currencyrateservice.exception.UnauthorizedUserException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyMockApiService {

    @Value("${currency-rate-server.fiat-url}")
    private String CURRENCY_MOCKS_FIAT_URL;

    @Value("${currency-rate-server.crypto-url}")
    private String CURRENCY_MOCKS_CRYPTO_URL;

    private final WebClient webClient;

    public Flux<FiatCurrencyRateDTO> fetchFiatRates() {
        return webClient.get()
                .uri(CURRENCY_MOCKS_FIAT_URL)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> {
                    log.error("Currency mocks FIAT API: Unauthorized access");
                    return Mono.error(new ServerConnectionException("Invalid API key"));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    log.info("Currency mocks FIAT API: Server error occurred");
                    return Mono.error(new UnauthorizedUserException("Currency mocks FIAT API Error"));
                })
                .bodyToFlux(FiatCurrencyRateDTO.class)
                .onErrorResume(UnauthorizedUserException.class, e -> {
                    log.info("Currency mocks FIAT API fallback due to: {}", e.getMessage());
                    return Flux.empty();
                });
    }

    public Flux<CryptoCurrencyRateDTO> fetchCryptoRates() {
        return webClient.get()
                .uri(CURRENCY_MOCKS_CRYPTO_URL)
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    log.info("Currency mocks CRYPTO API: Server error occurred");
                    return Mono.error(new UnauthorizedUserException("Currency mocks CRYPTO API Error"));
                })
                .bodyToFlux(CryptoCurrencyRateDTO.class)
                .onErrorResume(UnauthorizedUserException.class, e -> {
                    log.info("Currency mocks CRYPTO API fallback due to: {}", e.getMessage());
                    return Flux.empty();
                });
    }
}
