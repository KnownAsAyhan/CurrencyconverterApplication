package com.example.currencyconverter.controller;

import com.example.currencyconverter.model.CurrencyPair;
import com.example.currencyconverter.model.ExchangeRate;
import com.example.currencyconverter.repository.CurrencyPairRepository;
import com.example.currencyconverter.repository.ExchangeRateRepository;
import com.example.currencyconverter.service.ExcelExportService;
import com.example.currencyconverter.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CurrencyController {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyPairRepository currencyPairRepository;
    private final ExcelExportService excelExportService;

    @PostMapping("/currencies")
    public ResponseEntity<String> convertCurrency(@RequestParam String base,
                                                  @RequestParam String target,
                                                  @RequestParam double amount) {
        double rate = exchangeRateService.fetchAndSaveRate(base, target);
        double result = rate * amount;
        String response = String.format("%.2f %s → %s = %.2f %s", amount, base, target, result, target);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rates")
    public ResponseEntity<List<ExchangeRate>> getAllRates() {
        return ResponseEntity.ok(exchangeRateRepository.findAll());
    }

    @GetMapping("/rates/export")
    public ResponseEntity<byte[]> exportToExcel(@RequestParam String base,
                                                @RequestParam String target) {
        CurrencyPair pair = currencyPairRepository
                .findByBaseCurrencyAndTargetCurrency(base, target)
                .orElseThrow(() -> new RuntimeException("Currency pair not found"));

        try (InputStream inputStream = excelExportService.exportRatesToExcel(pair)) {
            byte[] bytes = inputStream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exchange_rates.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
