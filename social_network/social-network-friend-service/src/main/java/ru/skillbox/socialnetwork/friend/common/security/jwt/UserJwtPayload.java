package ru.skillbox.socialnetwork.friend.common.security.jwt;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJwtPayload {

    private UUID id;

}
