package ru.skillbox.socialnetwork.authentication.mappers;

import ru.skillbox.socialnetwork.authentication.entities.user.Role;
import ru.skillbox.socialnetwork.authentication.entities.user.User;
import ru.skillbox.socialnetwork.authentication.events.NewUserRegisteredEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    default NewUserRegisteredEvent userToNewUserRegisteredEvent(User user) {
        return NewUserRegisteredEvent.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toSet()))
                .password(user.getPassword())
                .build();
    }
}