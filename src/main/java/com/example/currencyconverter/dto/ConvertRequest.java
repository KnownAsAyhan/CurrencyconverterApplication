package com.example.currencyconverter.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class ConvertRequest {

    @NotBlank(message = "Base currency is required")
    @Size(min = 3, max = 3, message = "Base currency must be a 3-letter code")
    private String base;

    @NotBlank(message = "Target currency is required")
    @Size(min = 3, max = 3, message = "Target currency must be a 3-letter code")
    private String target;

    @Positive(message = "Amount must be greater than 0")
    private double amount;
}
