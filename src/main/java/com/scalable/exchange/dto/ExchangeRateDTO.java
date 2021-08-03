package com.scalable.exchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scalable.exchange.model.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
public class ExchangeRateDTO {

    private String symbol;
    private BigDecimal rate;
    private LocalDate date;

    public ExchangeRateDTO(String symbol) {
        this.symbol = symbol;
    }

    public static List<ExchangeRateDTO> of(List<String> exchangeRateList) {
        return exchangeRateList
                .stream()
                .map(ExchangeRateDTO::new)
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<ExchangeRateDTO> map(List<ExchangeRate> exchangeRateList) {
        return exchangeRateList
                .stream()
                .map(exchangeRate -> new ExchangeRateDTO(exchangeRate.getCurrencySymbol(),
                        exchangeRate.getValue(), exchangeRate.getDate()))
                .distinct()
                .collect(Collectors.toList());
    }
}
