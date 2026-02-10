package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.User.UserDto;
import com.example.hotel_booking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /api/v1/users | Получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        log.info("GET /api/v1/users/{} | Получение пользователя по Email", email);
        return userService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @GetMapping("/username/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        log.info("GET /api/v1/users/{} | Получение пользователя по username", username);
        return userService.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("POST /api/v1/users | Создание пользователя с email={}", userDto.getEmail());
        return userService.createUser(userDto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("PUT /api/v1/users/{} | Обновление пользователя", id);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} | Удаление пользователя", id);
        userService.deleteUser(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex) {
        return ex.getMessage();
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String identifier) {
            super("Пользователь '" + identifier + "' не найден");
        }
    }
}