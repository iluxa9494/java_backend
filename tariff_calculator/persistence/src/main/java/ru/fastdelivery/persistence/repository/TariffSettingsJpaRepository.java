package ru.fastdelivery.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fastdelivery.persistence.entity.TariffSettingsEntity;

import java.util.Optional;

public interface TariffSettingsJpaRepository extends JpaRepository<TariffSettingsEntity, Long> {
    Optional<TariffSettingsEntity> findTopByOrderByCreatedAtDesc();
}