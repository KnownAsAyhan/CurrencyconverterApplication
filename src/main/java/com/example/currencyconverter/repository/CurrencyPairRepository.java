package com.example.currencyconverter.repository;

import com.example.currencyconverter.model.CurrencyPair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyPairRepository extends JpaRepository<CurrencyPair, Long> {
    Optional<CurrencyPair> findByBaseCurrencyAndTargetCurrency(String baseCurrency, String targetCurrency);
}
