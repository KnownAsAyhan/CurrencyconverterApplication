package com.example.currencyconverter.service;

import com.example.currencyconverter.model.CurrencyPair;
import com.example.currencyconverter.model.ExchangeRate;
import com.example.currencyconverter.repository.CurrencyPairRepository;
import com.example.currencyconverter.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final CurrencyPairRepository currencyPairRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public double fetchAndSaveRate(String base, String target) {
        // 1. Build API URL
        String url = UriComponentsBuilder.fromHttpUrl("https://api.frankfurter.app/latest")
                .queryParam("from", base)
                .queryParam("to", target)
                .toUriString();

        // 2. Call Frankfurter API
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("rates")) {
            throw new RuntimeException("API error or currency not supported");
        }

        Map<String, Double> rates = (Map<String, Double>) response.get("rates");
        double rate = rates.get(target);

        // 3. Save or find currency pair
        CurrencyPair pair = currencyPairRepository
                .findByBaseCurrencyAndTargetCurrency(base, target)
                .orElseGet(() -> {
                    CurrencyPair newPair = new CurrencyPair();
                    newPair.setBaseCurrency(base);
                    newPair.setTargetCurrency(target);
                    return currencyPairRepository.save(newPair);
                });

        // 4. Save exchange rate
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrencyPair(pair);
        exchangeRate.setRate(rate);
        exchangeRate.setTimestamp(LocalDateTime.now());
        exchangeRateRepository.save(exchangeRate);

        return rate;
    }

    // 🔁 Scheduled method to fetch all stored pairs every 3 hours
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000) // every 3 hours
    public void fetchAllRatesScheduled() {
        System.out.println("⏰ Scheduled task: Fetching all rates...");

        List<CurrencyPair> pairs = currencyPairRepository.findAll();
        for (CurrencyPair pair : pairs) {
            try {
                fetchAndSaveRate(pair.getBaseCurrency(), pair.getTargetCurrency());
            } catch (Exception e) {
                System.err.println("❌ Error fetching rate for " + pair.getBaseCurrency() + " → " + pair.getTargetCurrency());
            }
        }
    }
}
