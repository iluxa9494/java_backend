package ru.fastdelivery.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.fastdelivery.domain.delivery.AdditionalService;
import ru.fastdelivery.domain.repository.AdditionalServiceRepository;
import ru.fastdelivery.persistence.mapper.EntityToDomainMapper;

import java.util.List;

/**
 * Адаптер репозитория дополнительных услуг.
 * Реализует доменный интерфейс, используя JPA-репозиторий.
 */
@Repository
@RequiredArgsConstructor
public class AdditionalServiceJpaAdapter implements AdditionalServiceRepository {
    private final AdditionalServiceJpaRepository repository;

    @Override
    public List<AdditionalService> getAll() {
        return repository.findAll()
                .stream()
                .map(EntityToDomainMapper::toDomain)
                .toList();
    }
}