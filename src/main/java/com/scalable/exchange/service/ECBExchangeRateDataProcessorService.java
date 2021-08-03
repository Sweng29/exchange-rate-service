package com.scalable.exchange.service;

import com.scalable.exchange.model.ExchangeRate;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ECBExchangeRateDataProcessorService {

    void loadAndProcessExchangeRateFromECB() throws IOException;

    boolean validateFileNotExists(Path path);

    List<ExchangeRate> processCSVData() throws IOException;

}
