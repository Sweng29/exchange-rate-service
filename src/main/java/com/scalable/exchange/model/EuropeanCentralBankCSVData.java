package com.scalable.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EuropeanCentralBankCSVData {

    private LocalDate date;
    private String countryCode;
    private BigDecimal exchangeRate;
}
