package com.example.hotel_booking.repository.jpa;

import com.example.hotel_booking.model.Role;
import com.example.hotel_booking.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
