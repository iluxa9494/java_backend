package ru.fastdelivery.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.repository.CurrencyRepository;
import ru.fastdelivery.persistence.mapper.EntityToDomainMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CurrencyJpaAdapter implements CurrencyRepository {
    private final CurrencyJpaRepository repository;

    @Override
    public Optional<Currency> findByCode(String code) {
        return repository.findByCode(code)
                .map(EntityToDomainMapper::toDomain);
    }
}