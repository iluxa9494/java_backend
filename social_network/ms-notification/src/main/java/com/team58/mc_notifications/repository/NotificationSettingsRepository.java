package com.team58.mc_notifications.repository;

import com.team58.mc_notifications.domain.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {

    Optional<NotificationSettings> findByUserId(UUID userId);
}