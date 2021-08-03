package com.scalable.exchange;

import com.scalable.exchange.service.ECBExchangeRateDataProcessorService;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories
@Slf4j
public class ExchangeRateServiceApplication {

    @Autowired
    private ECBExchangeRateDataProcessorService ecbExchangeRateDataProcessorService;

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateServiceApplication.class, args);
    }

    @PostConstruct
    private void postConstruct() throws IOException {
        ecbExchangeRateDataProcessorService.loadAndProcessExchangeRateFromECB();
    }

    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return new RestTemplate(factory);
    }

}
