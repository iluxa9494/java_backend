package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.User.UserDto;
import com.example.hotel_booking.mapper.UserMapper;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public Optional<UserDto> getUserByEmail(String email) {
        log.info("Поиск пользователя по email: {}", email);
        return userRepository.findByEmail(email).map(userMapper::toDto);
    }

    public Optional<UserDto> getUserByUsername(String username) {
        log.info("Поиск пользователя по username: {}", username);
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя с email: {}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }

        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя с id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        userRepository.deleteById(id);
    }
}
