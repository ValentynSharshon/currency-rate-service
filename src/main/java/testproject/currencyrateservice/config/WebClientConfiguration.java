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

    private final WebClient webClient;

    private final String secretKey;

    @Autowired
    public WebClientConfiguration(@Value("${currency-rate-server.url}") String baseUrl,
                                  @Value("${currency-rate-server.secret-key}") String secretKey) {
        this.secretKey = secretKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<FiatCurrencyRateDTO> fetchFiatRates() {
        return webClient.get()
                .uri("/fiat-currency-rates")
                .header("X-API-KEY", secretKey)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> {
                    log.error("Fiat API: Unauthorized access");
                    return Mono.error(new ServerConnectionException("Invalid API key"));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    log.info("Fiat API: Server error occurred");
                    return Mono.error(new UnauthorizedUserException("Fiat API Error"));
                })
                .bodyToFlux(FiatCurrencyRateDTO.class)
                .onErrorResume(UnauthorizedUserException.class, e -> {
                    log.info("Fiat API fallback due to: {}", e.getMessage());
                    return Flux.empty();
                });
    }

    public Flux<CryptoCurrencyRateDTO> fetchCryptoRates() {
        return webClient.get()
                .uri("/crypto-currency-rates")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    log.info("Crypto API: Server error occurred");
                    return Mono.error(new UnauthorizedUserException("Crypto API Error"));
                })
                .bodyToFlux(CryptoCurrencyRateDTO.class)
                .onErrorResume(UnauthorizedUserException.class, e -> {
                    log.info("Crypto API fallback due to: {}", e.getMessage());
                    return Flux.empty();
                });
    }

}
