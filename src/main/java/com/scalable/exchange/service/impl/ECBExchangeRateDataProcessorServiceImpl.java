package com.scalable.exchange.service.impl;

import com.scalable.exchange.helper.UnzipUtility;
import com.scalable.exchange.model.ExchangeRate;
import com.scalable.exchange.service.ECBExchangeRateDataProcessorService;
import com.scalable.exchange.service.ExchangeRateService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@Slf4j
public class ECBExchangeRateDataProcessorServiceImpl implements
        ECBExchangeRateDataProcessorService {

    private static final String DATE = "Date";
    private static final String NA = "N/A";
    private static final String EURO_SYMBOL = "EUR";
    @Value("${ecb.csv.url}")
    private String csvUrl;
    @Value("${ecb.csv.name}")
    private String csvFileName;
    @Value("${ecb.csv.file.name}")
    private String csvFile;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private UnzipUtility unzipUtility;

    @Override
    public void loadAndProcessExchangeRateFromECB() throws IOException {
        log.debug(
                "Inside ECBExchangeRateDataProcessorServiceImpl - loadAndProcessExchangeRateFromECB method [START]");
        var path = Paths.get(csvFileName);
        boolean notExists = validateFileNotExists(path);
        if (notExists) {
            downloadCSVFileFromECB(path);
            var zipFilePath = new ClassPathResource(csvFileName);
            unzipUtility.unzip(zipFilePath.getPath(), ".");
        }
        List<ExchangeRate> exchangeRateList = processCSVData();
        exchangeRateService.processCSVDataForH2Database(exchangeRateList);
        log.debug(
                "Inside ECBExchangeRateDataProcessorServiceImpl - loadAndProcessExchangeRateFromECB method [END]");
    }

    private void downloadCSVFileFromECB(Path path) {
        log.info("Inside ECBExchangeRateDataProcessorServiceImpl downloadCSVFileFromECB - [START]");
        log.info("Fetching Exchange rates from " + csvUrl);
        // Optional Accept header
        RequestCallback requestCallback = request -> request
                .getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        ResponseExtractor<Void> responseExtractor = response -> {
            if (Files.exists(path)) {
                Files.deleteIfExists(path);
            }
            Files.copy(response.getBody(), path);
            return null;
        };
        restTemplate.execute(csvUrl, HttpMethod.GET, requestCallback, responseExtractor);
        log.info("Inside ECBExchangeRateDataProcessorServiceImpl downloadCSVFileFromECB - [END]");
    }

    @Override
    public boolean validateFileNotExists(Path path) {
        return Files.notExists(path);
    }

    @Override
    @Async
    public List<ExchangeRate> processCSVData() throws IOException {
        log.info("Inside ECBExchangeRateDataProcessorServiceImpl processCSVData - [START]");
        log.info("Loading CSV file from Path and Processing.");
        var path = new File(new ClassPathResource(csvFile).getPath());
        InputStream inputStream = new FileInputStream(path);
        Reader reader = new InputStreamReader(inputStream);
        CSVParser records = CSVFormat
                .DEFAULT
                .withFirstRecordAsHeader()
                .withTrailingDelimiter()
                .parse(reader);
        List<ExchangeRate> exchangeRateList = retrieveCSVData(records);
        log.debug("TOTAL ROWS FETCHED " + exchangeRateList.size());
        log.debug("Inside ECBExchangeRateDataProcessorServiceImpl processCSVData - [END]");
        return exchangeRateList;
    }

    private List<ExchangeRate> retrieveCSVData(CSVParser records) {
        log.debug("Inside ECBExchangeRateDataProcessorServiceImpl retrieveCSVData - [START]");
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        Map<String, Integer> csvHeaderMap = records.getHeaderMap();
        for (CSVRecord csvRecord : records) {
            for (String key : csvHeaderMap.keySet()) {
                if (!key.equals(DATE)) {
                    exchangeRateList.add(buildExchangeRate(key, csvRecord));
                }
            }
            exchangeRateList.add(buildExchangeRateForEuro(EURO_SYMBOL, csvRecord));
        }
        log.debug("Inside ECBExchangeRateDataProcessorServiceImpl retrieveCSVData - [END]");
        return exchangeRateList;
    }

    private ExchangeRate buildExchangeRate(String key, CSVRecord csvRecord) {
        BigDecimal value = csvRecord.get(key).equals(NA) ?
                BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(csvRecord.get(key)));
        return ExchangeRate
                .builder()
                .currencySymbol(key)
                .value(value)
                .noOfTimeRequested(0)
                .date(LocalDate.parse(csvRecord.get(DATE)))
                .build();
    }

    private ExchangeRate buildExchangeRateForEuro(String key, CSVRecord csvRecord) {
        BigDecimal value = BigDecimal.ONE;
        return ExchangeRate
                .builder()
                .currencySymbol(key)
                .value(value)
                .noOfTimeRequested(0)
                .date(LocalDate.parse(csvRecord.get(DATE)))
                .build();
    }

}
