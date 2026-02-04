package ru.fastdelivery.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fastdelivery.persistence.entity.UserRequestEntity;

import java.util.UUID;

public interface UserRequestJpaRepository extends JpaRepository<UserRequestEntity, UUID> {
}
