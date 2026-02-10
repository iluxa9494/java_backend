package ru.fastdelivery.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fastdelivery.persistence.entity.AdditionalServiceEntity;

public interface AdditionalServiceJpaRepository extends JpaRepository<AdditionalServiceEntity, Integer> {
}