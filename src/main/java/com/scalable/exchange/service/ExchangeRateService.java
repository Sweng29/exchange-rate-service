package com.scalable.exchange.service;

import com.scalable.exchange.dto.CurrencyExchangeDTO;
import com.scalable.exchange.model.ExchangeRate;
import com.scalable.exchange.payload.ExchangeRateFilterAttribute;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExchangeRateService {

    void processCSVDataForH2Database(List<ExchangeRate> exchangeRateList);

    List<String> getAllAvailableCurrencies();

    ResponseEntity<CurrencyExchangeDTO> getExchangeRateGivenFilterAttributes(ExchangeRateFilterAttribute exchangeRateFilterAttribute);

}
