package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Role.RoleDto;
import com.example.hotel_booking.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleDto toDto(Role role);

    @Mapping(target = "id", ignore = true)
    Role toEntity(RoleDto roleDto);
}