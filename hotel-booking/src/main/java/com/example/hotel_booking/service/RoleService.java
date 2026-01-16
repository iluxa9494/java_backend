package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Role.RoleDto;
import com.example.hotel_booking.mapper.RoleMapper;
import com.example.hotel_booking.model.Role;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.repository.jpa.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDto> getAllRoles() {
        log.info("Получение всех ролей");
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public Optional<RoleDto> getRoleByName(RoleType name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toDto);
    }

    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        log.info("Создание новой роли: {}", roleDto.getName());

        if (roleRepository.findByName(roleDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Роль уже существует");
        }

        Role role = roleMapper.toEntity(roleDto);
        return roleMapper.toDto(roleRepository.save(role));
    }

    public void deleteRole(Long id) {
        log.info("Удаление роли с ID {}", id);
        roleRepository.deleteById(id);
        log.info("Роль с ID {} успешно удалена", id);
    }
}
