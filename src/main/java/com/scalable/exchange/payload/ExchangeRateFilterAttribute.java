package com.scalable.exchange.payload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRateFilterAttribute {

    private String baseCurrency;
    private List<String> symbols;
    @NotNull(message = "Please provide a valid date.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private BigDecimal amount;

}
