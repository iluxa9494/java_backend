package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Role.RoleDto;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        List<RoleDto> roles = List.of(new RoleDto(1L, RoleType.ADMIN));
        when(roleService.getAllRoles()).thenReturn(roles);

        List<RoleDto> result = roleController.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RoleType.ADMIN, result.get(0).getName());
        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void testGetRoleByName_WhenRoleExists() {
        RoleDto roleDto = new RoleDto(1L, RoleType.USER);
        when(roleService.getRoleByName(RoleType.USER)).thenReturn(Optional.of(roleDto));

        RoleDto result = roleController.getRoleByName(RoleType.USER);

        assertNotNull(result);
        assertEquals(RoleType.USER, result.getName());
        verify(roleService, times(1)).getRoleByName(RoleType.USER);
    }

    @Test
    void testGetRoleByName_WhenRoleDoesNotExist() {
        when(roleService.getRoleByName(RoleType.MANAGER)).thenReturn(Optional.empty());

        RoleController.RoleNotFoundException exception = assertThrows(
                RoleController.RoleNotFoundException.class,
                () -> roleController.getRoleByName(RoleType.MANAGER)
        );

        assertEquals("Роль 'MANAGER' не найдена", exception.getMessage());
        verify(roleService, times(1)).getRoleByName(RoleType.MANAGER);
    }

    @Test
    void testCreateRole() {
        RoleDto requestDto = new RoleDto(null, RoleType.MANAGER);
        RoleDto responseDto = new RoleDto(2L, RoleType.MANAGER);
        when(roleService.createRole(requestDto)).thenReturn(responseDto);

        RoleDto result = roleController.createRole(requestDto);

        assertNotNull(result);
        assertEquals(RoleType.MANAGER, result.getName());
        verify(roleService, times(1)).createRole(requestDto);
    }

    @Test
    void testDeleteRole() {
        doNothing().when(roleService).deleteRole(1L);
        roleController.deleteRole(1L);
        verify(roleService, times(1)).deleteRole(1L);
    }
}
