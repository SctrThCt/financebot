package stc.monitoring.financebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import stc.monitoring.financebot.dto.RateDto;
import stc.monitoring.financebot.model.Rate;
import stc.monitoring.financebot.repository.CurrencyRepository;
import stc.monitoring.financebot.repository.RateRepository;
import stc.monitoring.financebot.util.RatesUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RateService {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;


    private static final String ACCESS_KEY = "1e7fa1cc6fd6ebe44465092e8c069353";

    public List<Rate> getCurrencyRates() {
        String uri = "http://apilayer.net/api/live?access_key=";
        StringBuilder quotes = new StringBuilder("&currencies=");
        currencyRepository.getAllByInUseTrue().forEach(currency -> quotes.append(currency.getCode() + ","));
        quotes.deleteCharAt(quotes.length() - 1);
        quotes.append("&source=USD&format=1");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + ACCESS_KEY + quotes.toString()))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<RateDto> rateDtoList = RatesUtil.parseRatesFromResponse(response.body());
            List<Rate> rates = new ArrayList<>();
            rateDtoList.forEach(rateDto -> rates.add(new Rate(
                                    currencyRepository.findCurrencyByCode(rateDto.currency()),
                                    rateDto.rate(),
                                    rateDto.date()
                            )
                    )
            );
            return rateRepository.saveAll(rates);
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }
    }

}
