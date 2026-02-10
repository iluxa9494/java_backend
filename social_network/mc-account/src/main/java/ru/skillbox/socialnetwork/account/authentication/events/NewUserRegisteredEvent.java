package ru.skillbox.socialnetwork.account.authentication.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.account.authentication.entities.user.RoleType;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRegisteredEvent {
    private UUID id;
    private String email;
    private String password;
    private String lastName;
    private String firstName;
    private Set<RoleType> roles;
}
