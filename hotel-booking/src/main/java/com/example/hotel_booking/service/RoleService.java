package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.RoleDto;
import com.example.hotel_booking.mapper.RoleMapper;
import com.example.hotel_booking.model.Role;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public Optional<RoleDto> getRoleByName(RoleType name) {
        return roleRepository.findByName(name).map(roleMapper::toDto);
    }

    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        if (roleRepository.findByName(roleDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Роль уже существует");
        }
        Role role = roleMapper.toEntity(roleDto);
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
