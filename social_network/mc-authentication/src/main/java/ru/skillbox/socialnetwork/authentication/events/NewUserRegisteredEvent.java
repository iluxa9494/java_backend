package ru.skillbox.socialnetwork.authentication.events;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.authentication.entities.user.RoleType;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class NewUserRegisteredEvent {

    private UUID id;

    private String email;

    private String password;

    private String lastName;

    private String firstName;

    private Set<RoleType> roles;
}