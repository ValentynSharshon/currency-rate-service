package testproject.currencyrateservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testproject.currencyrateservice.config.WebClientConfiguration;
import testproject.currencyrateservice.dto.CurrencyRatesResponseDTO;
import testproject.currencyrateservice.model.CurrencyRate;
import testproject.currencyrateservice.model.CurrencyType;
import testproject.currencyrateservice.repository.CurrencyRateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final WebClientConfiguration webClientConfiguration;
    private final CurrencyRateRepository currencyRateRepository;

    public Mono<CurrencyRatesResponseDTO> getCurrencyRates() {
        Flux<CurrencyRate> fiatRates = webClientConfiguration.fetchFiatRates()
                .map(dto -> CurrencyRate.builder()
                        .currency(dto.getCurrency())
                        .rate(dto.getRate())
                        .type(CurrencyType.FIAT)
                        .lastUpdate(LocalDateTime.now())
                        .build())
                .flatMap(rate -> currencyRateRepository.findByCurrencyAndType(rate.getCurrency(), CurrencyType.FIAT)
                        .flatMap(existingRate -> {
                            existingRate.setRate(rate.getRate());
                            existingRate.setLastUpdate(rate.getLastUpdate());
                            return currencyRateRepository.save(existingRate);
                        })
                        .switchIfEmpty(currencyRateRepository.save(rate)))
                .switchIfEmpty(currencyRateRepository.findAllByType(CurrencyType.FIAT));

        Flux<CurrencyRate> cryptoRates = webClientConfiguration.fetchCryptoRates()
                .map(dto -> CurrencyRate.builder()
                        .currency(dto.getName())
                        .rate(dto.getValue())
                        .type(CurrencyType.CRYPTO)
                        .lastUpdate(LocalDateTime.now())
                        .build())
                .flatMap(rate -> currencyRateRepository.findByCurrencyAndType(rate.getCurrency(), CurrencyType.CRYPTO)
                        .flatMap(existingRate -> {
                            existingRate.setRate(rate.getRate());
                            existingRate.setLastUpdate(rate.getLastUpdate());
                            return currencyRateRepository.save(existingRate);
                        })
                        .switchIfEmpty(currencyRateRepository.save(rate)))
                .switchIfEmpty(currencyRateRepository.findAllByType(CurrencyType.CRYPTO));

        return Mono.zip(
                fiatRates.collectList(),
                cryptoRates.collectList(),
                (fiatList, cryptoList) -> new CurrencyRatesResponseDTO(
                        mapToResponse(fiatList),
                        mapToResponse(cryptoList)
                )
        );
    }


    private List<CurrencyRatesResponseDTO.CurrencyRate> mapToResponse(List<CurrencyRate> rates) {
        return rates.stream()
                .map(rate -> new CurrencyRatesResponseDTO.CurrencyRate(rate.getCurrency(), rate.getRate()))
                .collect(Collectors.toList());
    }

}
