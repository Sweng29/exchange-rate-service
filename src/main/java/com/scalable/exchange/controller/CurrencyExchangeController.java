package com.scalable.exchange.controller;

import com.scalable.exchange.dto.CurrencyExchangeDTO;
import com.scalable.exchange.dto.ExchangeRateDTO;
import com.scalable.exchange.payload.ExchangeRateFilterAttribute;
import com.scalable.exchange.service.ExchangeRateService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("v1/api/exchange-rate")
public class CurrencyExchangeController {

    @Value("${public.url.chart}")
    private String PUBLIC_URL_FOR_CHAR;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<CurrencyExchangeDTO> fetchExchangeRateGivenTargetCurrency(
            @Valid @NotNull ExchangeRateFilterAttribute filterAttribute) {
        return exchangeRateService.getExchangeRateGivenFilterAttributes(filterAttribute);
    }

    @GetMapping(value = "/available-currency/list")
    public ResponseEntity<List<ExchangeRateDTO>> fetchAllAvailableCurrency() {
        return ResponseEntity
                .ok(ExchangeRateDTO.of(exchangeRateService.getAllAvailableCurrencies()));
    }

    @GetMapping(value = "/view-chart/{from}/{to}", produces = "text/html;charset=UTF-8")
    public RedirectView viewChartForGivenPair(@PathVariable String from, @PathVariable String to) {
        var redirectView = new RedirectView();
        String newUrl = PUBLIC_URL_FOR_CHAR + "?from=" + from + "&to=" + to;
        redirectView.setUrl(newUrl);
        return redirectView;
    }

}
