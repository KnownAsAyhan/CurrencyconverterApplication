package com.example.currencyconverter.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pair_id", nullable = false)
    private CurrencyPair currencyPair;

    @Column(nullable = false)
    private double rate;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
