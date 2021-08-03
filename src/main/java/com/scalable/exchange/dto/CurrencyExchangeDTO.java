package com.scalable.exchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scalable.exchange.model.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyExchangeDTO {

    private String baseCurrency;
    private BigDecimal amount;
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public static Map<String, BigDecimal> map(List<ExchangeRate> exchangeRateList,
            BigDecimal amount) {
        return exchangeRateList
                .stream()
                .collect(
                        Collectors
                                .toMap(ExchangeRate::getCurrencySymbol, exchangeRate -> exchangeRate
                                        .getValue()
                                        .multiply(amount)));
    }
}
