package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RoleDto;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class RoleControllerTest {

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
        ResponseEntity<List<RoleDto>> response = roleController.getAllRoles();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(RoleType.ADMIN, response.getBody().get(0).getName());
        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void testGetRoleByName_WhenRoleExists() {
        RoleDto roleDto = new RoleDto(1L, RoleType.USER);
        when(roleService.getRoleByName(RoleType.USER)).thenReturn(Optional.of(roleDto));
        ResponseEntity<RoleDto> response = roleController.getRoleByName(RoleType.USER);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RoleType.USER, response.getBody().getName());
        verify(roleService, times(1)).getRoleByName(RoleType.USER);
    }

    @Test
    void testGetRoleByName_WhenRoleDoesNotExist() {
        when(roleService.getRoleByName(RoleType.MANAGER)).thenReturn(Optional.empty());
        ResponseEntity<RoleDto> response = roleController.getRoleByName(RoleType.MANAGER);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(roleService, times(1)).getRoleByName(RoleType.MANAGER);
    }

    @Test
    void testCreateRole() {
        RoleDto requestDto = new RoleDto(null, RoleType.MANAGER);
        RoleDto responseDto = new RoleDto(2L, RoleType.MANAGER);
        when(roleService.createRole(requestDto)).thenReturn(responseDto);
        ResponseEntity<RoleDto> response = roleController.createRole(requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(RoleType.MANAGER, response.getBody().getName());
        verify(roleService, times(1)).createRole(requestDto);
    }

    @Test
    void testDeleteRole() {
        doNothing().when(roleService).deleteRole(1L);
        ResponseEntity<Void> response = roleController.deleteRole(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleService, times(1)).deleteRole(1L);
    }
}