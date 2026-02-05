package ru.fastdelivery.domain.repository;

import ru.fastdelivery.domain.delivery.AdditionalService;

import java.util.List;

public interface AdditionalServiceRepository {
    List<AdditionalService> getAll();
}