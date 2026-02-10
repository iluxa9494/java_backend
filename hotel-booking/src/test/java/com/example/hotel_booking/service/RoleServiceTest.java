package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Role.RoleDto;
import com.example.hotel_booking.mapper.RoleMapper;
import com.example.hotel_booking.model.Role;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        List<Role> roles = List.of(new Role(1L, RoleType.ADMIN, null));
        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDto(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            return new RoleDto(role.getId(), role.getName());
        });
        List<RoleDto> result = roleService.getAllRoles();
        assertEquals(1, result.size());
        assertEquals(RoleType.ADMIN, result.get(0).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetRoleByName_WhenRoleExists() {
        Role role = new Role(1L, RoleType.USER, null);
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(new RoleDto(role.getId(), role.getName()));
        Optional<RoleDto> result = roleService.getRoleByName(RoleType.USER);
        assertTrue(result.isPresent());
        assertEquals(RoleType.USER, result.get().getName());
        verify(roleRepository, times(1)).findByName(RoleType.USER);
    }

    @Test
    void testGetRoleByName_WhenRoleDoesNotExist() {
        when(roleRepository.findByName(RoleType.MANAGER)).thenReturn(Optional.empty());
        Optional<RoleDto> result = roleService.getRoleByName(RoleType.MANAGER);
        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findByName(RoleType.MANAGER);
    }

    @Test
    void testCreateRole_WhenRoleDoesNotExist() {
        RoleDto roleDto = new RoleDto(null, RoleType.MANAGER);
        Role role = new Role(2L, RoleType.MANAGER, null);
        when(roleRepository.findByName(RoleType.MANAGER)).thenReturn(Optional.empty());
        when(roleMapper.toEntity(roleDto)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(new RoleDto(role.getId(), role.getName()));
        RoleDto result = roleService.createRole(roleDto);
        assertNotNull(result);
        assertEquals(RoleType.MANAGER, result.getName());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testCreateRole_WhenRoleAlreadyExists() {
        RoleDto roleDto = new RoleDto(null, RoleType.ADMIN);
        Role role = new Role(1L, RoleType.ADMIN, null);
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(Optional.of(role));
        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(roleDto));
        verify(roleRepository, times(1)).findByName(RoleType.ADMIN);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDeleteRole() {
        Long roleId = 1L;
        doNothing().when(roleRepository).deleteById(roleId);
        roleService.deleteRole(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }
}
