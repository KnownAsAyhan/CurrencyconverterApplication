package com.example.currencyconverter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "currency_pairs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_currency", nullable = false)
    private String baseCurrency;

    @Column(name = "target_currency", nullable = false)
    private String targetCurrency;
}
