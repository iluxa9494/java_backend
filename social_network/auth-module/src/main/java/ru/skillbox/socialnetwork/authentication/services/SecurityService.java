package ru.skillbox.socialnetwork.authentication.services;

import ru.skillbox.socialnetwork.authentication.events.ChangedEmailEvent;
import ru.skillbox.socialnetwork.authentication.events.ChangedPasswordEvent;
import ru.skillbox.socialnetwork.authentication.web.models.*;

public interface SecurityService {

    void register(CreateUserRequest createUserRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout();

    LoginResponse authenticateUser(LoginRequest loginRequest);

    Boolean validateToken(String token);

    Response changePassword(ChangedPasswordEvent event);

    Response changeEmail(ChangedEmailEvent event);

    Response recoveryPassword(RecoveryPassportRequest request);
}
