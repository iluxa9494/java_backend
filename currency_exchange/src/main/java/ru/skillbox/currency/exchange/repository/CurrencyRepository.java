package ru.skillbox.currency.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.currency.exchange.entity.Currency;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByIsoNumCode(Integer isoNumCode);

    Optional<Currency> findByIsoCharCode(String isoCharCode);

    List<Currency> findAllByOrderByCreatedAtDesc();
}