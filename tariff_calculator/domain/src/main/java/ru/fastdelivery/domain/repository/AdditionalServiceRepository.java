package ru.fastdelivery.domain.repository;

import ru.fastdelivery.domain.delivery.AdditionalService;

import java.util.List;

/**
 * Репозиторий для получения доступных дополнительных услуг.
 */
public interface AdditionalServiceRepository {
    List<AdditionalService> getAll();
}