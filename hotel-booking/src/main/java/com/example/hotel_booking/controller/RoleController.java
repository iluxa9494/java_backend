package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Role.RoleDto;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDto> getAllRoles() {
        log.info("GET /api/v1/roles | Получение списка всех ролей");
        return roleService.getAllRoles();
    }

    @GetMapping("/{name}")
    public RoleDto getRoleByName(@PathVariable RoleType name) {
        log.info("GET /api/v1/roles/{} | Получение роли по имени", name);
        return roleService.getRoleByName(name).orElseThrow(() -> new RoleNotFoundException(name));
    }

    @PostMapping
    public RoleDto createRole(@RequestBody RoleDto roleDto) {
        log.info("POST /api/v1/roles | Создание новой роли: {}", roleDto.getName());
        return roleService.createRole(roleDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long id) {
        log.info("DELETE /api/v1/roles/{} | Удаление роли", id);
        roleService.deleteRole(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RoleNotFoundException.class)
    public String handleRoleNotFound(RoleNotFoundException ex) {
        return ex.getMessage();
    }

    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(RoleType name) {
            super("Роль '" + name + "' не найдена");
        }
    }
}