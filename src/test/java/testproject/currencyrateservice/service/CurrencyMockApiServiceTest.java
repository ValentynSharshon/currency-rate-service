package testproject.currencyrateservice.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testproject.currencyrateservice.dto.FiatCurrencyRateDTO;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class CurrencyMockApiServiceTest {

    private static final String FAKE_URL = "/";
    private static final String FAKE_HEADER_NAME = "Content-Type";
    private static final String FAKE_HEADER_VALUE = "application/json";
    private static final String CURRENCY_CODE_USD = "USD";
    private static final String FAKE_RESPONSE_BODY = "[{\"currency\":\"USD\", \"rate\":1.1}]";


    private MockWebServer mockWebServer;
    private CurrencyMockApiService currencyMockApiService;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url(FAKE_URL).toString())
                .build();

        currencyMockApiService = new CurrencyMockApiService(webClient);
    }

    @Test
    void testFetchFiatRates_Success() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(FAKE_RESPONSE_BODY)
                .addHeader(FAKE_HEADER_NAME, FAKE_HEADER_VALUE)
                .setResponseCode(HttpStatus.OK.value()));

        Mono<FiatCurrencyRateDTO> response = currencyMockApiService.fetchFiatRates().next();

        StepVerifier.create(response)
                .expectNextMatches(dto -> dto.getCurrency().equals(CURRENCY_CODE_USD)
                        && dto.getRate().equals(BigDecimal.valueOf(1.1)))
                .verifyComplete();
    }

    @Test
    void testFetchFiatRates_Error() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .addHeader(FAKE_HEADER_NAME, FAKE_HEADER_VALUE));

        Mono<FiatCurrencyRateDTO> response = currencyMockApiService.fetchFiatRates().next();

        StepVerifier.create(response)
                .expectComplete()
                .verify();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

}
