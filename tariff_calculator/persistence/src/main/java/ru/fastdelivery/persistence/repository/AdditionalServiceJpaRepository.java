package ru.fastdelivery.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fastdelivery.persistence.entity.AdditionalServiceEntity;

/**
 * JPA-репозиторий для доступа к сущностям дополнительных услуг.
 * Позволяет выполнять стандартные CRUD-операции.
 */
public interface AdditionalServiceJpaRepository extends JpaRepository<AdditionalServiceEntity, Integer> {
}