package testproject.currencyrateservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testproject.currencyrateservice.dto.CryptoCurrencyRateDTO;
import testproject.currencyrateservice.dto.FiatCurrencyRateDTO;
import testproject.currencyrateservice.exception.ServerConnectionException;
import testproject.currencyrateservice.exception.UnauthorizedUserException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientConfiguration {

    private static final String CURRENCY_MOCKS_FIAT_URL = "/fiat-currency-rates";
    private static final String CURRENCY_MOCKS_CRYPTO_URL = "/crypto-currency-rates";
    private static final String SECRET_KEY_HEADER = "X-API-KEY";

    private final WebClient webClient;
    private final String secretKey;

    @Autowired
    public WebClientConfiguration(
            @Value("${currency-rate-server.url}") String baseUrl,
            @Value("${currency-rate-server.secret-key}") String secretKey
    ) {
        this.secretKey = secretKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<FiatCurrencyRateDTO> fetchFiatRates() {
        return webClient.get()
                .uri(CURRENCY_MOCKS_FIAT_URL)
                .header(SECRET_KEY_HEADER, secretKey)
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
