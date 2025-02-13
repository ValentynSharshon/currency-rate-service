package testproject.currencyrateservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient(@Value("${currency-rate-server.url}") String baseUrl,
                               @Value("${currency-rate-server.header-name}") String headerName,
                               @Value("${currency-rate-server.secret-key}") String secretKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(headerName, secretKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
