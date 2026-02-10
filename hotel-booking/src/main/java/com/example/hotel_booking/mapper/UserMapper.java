package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.User.UserDto;
import com.example.hotel_booking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(target = "role", source = "role.name"),
            @Mapping(target = "createdAt", ignore = true)
    })
    UserDto toDto(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "role", ignore = true)
    })
    User toEntity(UserDto userDto);
}