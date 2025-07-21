package com.example.currencyconverter.repository;

import com.example.currencyconverter.model.ExchangeRate;
import com.example.currencyconverter.model.CurrencyPair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    List<ExchangeRate> findByCurrencyPairAndTimestampBetween(CurrencyPair currencyPair, LocalDateTime start, LocalDateTime end);
}
