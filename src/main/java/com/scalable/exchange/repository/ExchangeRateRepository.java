package com.scalable.exchange.repository;

import com.scalable.exchange.model.ExchangeRate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("FROM ExchangeRate er where (:localDate IS NULL OR er.date = :localDate)")
    List<ExchangeRate> findAllByDate(LocalDate localDate);

    @Query("SELECT distinct er FROM ExchangeRate er where (:localDate IS NULL OR er.date = :localDate) "
            + "AND er.currencySymbol IN (:symbolList)")
    List<ExchangeRate> findAllByCurrencySymbolOrDate(LocalDate localDate, List<String> symbolList);

    Optional<ExchangeRate> findByCurrencySymbol(String currencySymbol);

    @Query("SELECT DISTINCT(er.currencySymbol) FROM ExchangeRate er")
    List<String> findAllAvailableCurrencies();

    @Transactional
    @Modifying
    @Query("UPDATE ExchangeRate er SET er.noOfTimeRequested = er.noOfTimeRequested + 1 " +
            "WHERE (:localDate IS NULL OR er.date = :localDate) " +
            "AND er.currencySymbol IN (:symbolList)")
    void updateRequestCounter(LocalDate localDate, List<String> symbolList);
}
