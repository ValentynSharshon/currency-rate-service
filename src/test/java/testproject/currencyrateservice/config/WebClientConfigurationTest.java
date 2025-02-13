package testproject.currencyrateservice.config;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class WebClientConfigurationTest {

    private static final String FAKE_URL = "/";
    private static final String FAKE_SECRET = "fake-secret-key";
    private static final String HEADER_NAME = "Content-Type";
    private static final String HEADER_VALUE = "application/json";

    private MockWebServer mockWebServer;
    private WebClientConfiguration webClientConfiguration;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url(FAKE_URL).toString())
                .build();

        webClientConfiguration = new WebClientConfiguration(webClient, FAKE_SECRET);
    }

    @Test
    void testFetchFiatRates_Success() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[{\"currency\":\"USD\", \"rate\":1.1}]")
                .addHeader(HEADER_NAME, HEADER_VALUE)
                .setResponseCode(HttpStatus.OK.value()));

        var response = webClientConfiguration.fetchFiatRates().next();

        StepVerifier.create(response)
                .expectNextMatches(dto -> dto.getCurrency().equals("USD")
                        && dto.getRate().equals(BigDecimal.valueOf(1.1)))
                .verifyComplete();
    }

    @Test
    void testFetchFiatRates_Error() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .addHeader(HEADER_NAME, HEADER_VALUE));

        var response = webClientConfiguration.fetchFiatRates().next();

        StepVerifier.create(response)
                .expectComplete()
                .verify();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

}
