package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.User.UserDto;
import com.example.hotel_booking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        List<UserDto> users = List.of(
                new UserDto(1L, "user1", "user1@example.com", "password", "USER", "2025-03-19")
        );
        when(userService.getAllUsers()).thenReturn(users);

        List<UserDto> result = userController.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserByEmail() {
        UserDto userDto = new UserDto(1L, "user1", "user1@example.com", "password", "USER", "2025-03-19");
        when(userService.getUserByEmail("user1@example.com")).thenReturn(Optional.of(userDto));

        UserDto result = userController.getUserByEmail("user1@example.com");

        assertEquals("user1", result.getUsername());
        verify(userService, times(1)).getUserByEmail("user1@example.com");
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto(null, "newUser", "new@example.com", "password", "USER", "2025-03-19");
        when(userService.createUser(userDto)).thenReturn(userDto);

        UserDto result = userController.createUser(userDto);

        assertEquals("newUser", result.getUsername());
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);
        userController.deleteUser(1L);
        verify(userService, times(1)).deleteUser(1L);
    }
}
