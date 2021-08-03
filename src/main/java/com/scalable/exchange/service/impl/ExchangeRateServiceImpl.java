package com.scalable.exchange.service.impl;

import com.scalable.exchange.dto.CurrencyExchangeDTO;
import com.scalable.exchange.exception.NoDataFoundException;
import com.scalable.exchange.model.ExchangeRate;
import com.scalable.exchange.payload.ExchangeRateFilterAttribute;
import com.scalable.exchange.repository.ExchangeRateRepository;
import com.scalable.exchange.service.ExchangeRateService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String EURO = "EUR";

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Override
    @Async
    public void processCSVDataForH2Database(List<ExchangeRate> exchangeRateList) {
        exchangeRateRepository.saveAll(exchangeRateList);
    }

    @Override
    public List<String> getAllAvailableCurrencies() {
        return exchangeRateRepository.findAllAvailableCurrencies();
    }

    @Override
    public ResponseEntity<CurrencyExchangeDTO> getExchangeRateGivenFilterAttributes(
            ExchangeRateFilterAttribute filterAttribute) {

        populateFilterAttributes(filterAttribute);

        List<ExchangeRate> exchangeRateList = exchangeRateRepository.findAllByCurrencySymbolOrDate(
                filterAttribute.getDate(),
                filterAttribute.getSymbols());

        if (ObjectUtils.isEmpty(exchangeRateList)) {
            throw new NoDataFoundException("No data found.");
        }

        exchangeRateRepository
                .updateRequestCounter(filterAttribute.getDate(), filterAttribute.getSymbols());

        if (ObjectUtils.isEmpty(filterAttribute.getBaseCurrency()) || filterAttribute
                .getBaseCurrency().trim().equalsIgnoreCase(EURO)) {
            return processExchangeRateWhenNoBaseCurrencyProvided(exchangeRateList,
                    filterAttribute.getAmount());
        }

        return processExchangeRateWhenBaseCurrencyProvided(exchangeRateList, filterAttribute);
    }

    private void populateFilterAttributes(ExchangeRateFilterAttribute filterAttribute) {
        // When user don't provide any symbol fetch all the available currencies available
        if (ObjectUtils.isEmpty(filterAttribute.getSymbols())) {
            List<String> currencySymbolList = getAllAvailableCurrencies();
            filterAttribute.setSymbols(currencySymbolList);
        }

        if (!ObjectUtils.isEmpty(filterAttribute.getBaseCurrency())) {
            filterAttribute.getSymbols().add(filterAttribute.getBaseCurrency());
        }

        if (ObjectUtils.isEmpty(filterAttribute.getAmount())) {
            filterAttribute.setAmount(BigDecimal.ONE);
        }
    }

    private ResponseEntity<CurrencyExchangeDTO> processExchangeRateWhenBaseCurrencyProvided(
            List<ExchangeRate> exchangeRateList,
            ExchangeRateFilterAttribute filterAttribute) {

        ExchangeRate baseCurrency = exchangeRateList
                .stream()
                .filter(exchangeRate -> exchangeRate.getCurrencySymbol()
                        .equalsIgnoreCase(filterAttribute.getBaseCurrency()))
                .findAny()
                .orElseThrow();

        BigDecimal baseRateToEuro = BigDecimal.ONE
                .divide(baseCurrency.getValue(), 6, RoundingMode.HALF_UP);
        List<ExchangeRate> exchangeRatesToBaseCurrency = new ArrayList<>();
        exchangeRateList
                .stream()
                .filter(exchangeRate -> !exchangeRate.getCurrencySymbol()
                        .equals(baseCurrency.getCurrencySymbol()))
                .forEach(exchangeRate -> {
                    BigDecimal newValueToBaseCurrency = exchangeRate.getValue()
                            .multiply(baseRateToEuro);
                    var exchangeRate1 = ExchangeRate
                            .builder()
                            .currencySymbol(exchangeRate.getCurrencySymbol())
                            .date(exchangeRate.getDate())
                            .value(newValueToBaseCurrency)
                            .build();
                    exchangeRatesToBaseCurrency.add(exchangeRate1);
                });
        var currencyExchangeDTO =
                CurrencyExchangeDTO
                        .builder()
                        .baseCurrency(filterAttribute.getBaseCurrency())
                        .amount(filterAttribute.getAmount())
                        .date(filterAttribute.getDate())
                        .rates(CurrencyExchangeDTO
                                .map(exchangeRatesToBaseCurrency, filterAttribute.getAmount()))
                        .build();
        return ResponseEntity.ok(currencyExchangeDTO);
    }

    private ResponseEntity<CurrencyExchangeDTO> processExchangeRateWhenNoBaseCurrencyProvided(
            List<ExchangeRate> exchangeRateList,
            BigDecimal amount) {
        var currencyExchangeDTO = CurrencyExchangeDTO
                .builder()
                .baseCurrency(EURO)
                .amount(amount)
                .rates(CurrencyExchangeDTO.map(exchangeRateList, amount))
                .build();
        return ResponseEntity.ok(currencyExchangeDTO);
    }
}
