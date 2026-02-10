package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.User.UserDto;
import com.example.hotel_booking.mapper.UserMapper;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(new User(1L, "user1", "user1@example.com", "password", null, null));
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenReturn(new UserDto(1L, "user1", "user1@example.com", "password", "USER", "2025-03-19"));
        List<UserDto> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByEmail_WhenExists() {
        User user = new User(1L, "user1", "user1@example.com", "password", null, null);
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto(1L, "user1", "user1@example.com", "password", "USER", "2025-03-19"));
        Optional<UserDto> result = userService.getUserByEmail("user1@example.com");
        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
        verify(userRepository, times(1)).findByEmail("user1@example.com");
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto(null, "newUser", "new@example.com", "password", "USER", "2025-03-19");
        User user = new User(1L, "newUser", "new@example.com", "encodedPassword", null, null);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(new UserDto(1L, "newUser", "new@example.com", "password", "USER", "2025-03-19"));
        UserDto result = userService.createUser(userDto);
        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
