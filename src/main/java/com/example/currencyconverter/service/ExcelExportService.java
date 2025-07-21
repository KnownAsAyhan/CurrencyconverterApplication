package com.example.currencyconverter.service;

import com.example.currencyconverter.model.CurrencyPair;
import com.example.currencyconverter.model.ExchangeRate;
import com.example.currencyconverter.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final ExchangeRateRepository exchangeRateRepository;

    public InputStream exportRatesToExcel(CurrencyPair pair) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyPairAndTimestampBetween(
                pair, sevenDaysAgo, now
        );

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Rates");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Timestamp");
            header.createCell(1).setCellValue("Rate");

            int rowIdx = 1;
            for (ExchangeRate rate : rates) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rate.getTimestamp().toString());
                row.createCell(1).setCellValue(rate.getRate());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }
}
