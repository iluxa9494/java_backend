package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.UserDto;
import com.example.hotel_booking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> users = List.of(new UserDto(1L, "user1", "user1@example.com", "password", "USER", "2025-03-19"));
        when(userService.getAllUsers()).thenReturn(users);
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();
        assertEquals(1, response.getBody().size());
        assertEquals("user1", response.getBody().get(0).getUsername());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserByEmail() {
        UserDto userDto = new UserDto(1L, "user1", "user1@example.com", "password", "USER", "2025-03-19");
        when(userService.getUserByEmail("user1@example.com")).thenReturn(Optional.of(userDto));
        ResponseEntity<UserDto> response = userController.getUserByEmail("user1@example.com");
        assertEquals("user1", response.getBody().getUsername());
        verify(userService, times(1)).getUserByEmail("user1@example.com");
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto(null, "newUser", "new@example.com", "password", "USER", "2025-03-19");
        when(userService.createUser(userDto)).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.createUser(userDto);
        assertEquals("newUser", response.getBody().getUsername());
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(1L);
    }
}
