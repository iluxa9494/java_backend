package ru.fastdelivery.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fastdelivery.persistence.entity.CurrencyEntity;

import java.util.Optional;

/**
 * JPA-репозиторий для работы с сущностями валют.
 * Предоставляет методы для поиска валюты по коду.
 */
public interface CurrencyJpaRepository extends JpaRepository<CurrencyEntity, String> {
    Optional<CurrencyEntity> findByCode(String code);
}