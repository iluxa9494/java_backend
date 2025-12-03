package ru.skillbox.socialnetwork.authentication.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.authentication.entities.PasswordResetToken;
import ru.skillbox.socialnetwork.authentication.entities.RefreshToken;
import ru.skillbox.socialnetwork.authentication.entities.user.Role;
import ru.skillbox.socialnetwork.authentication.entities.user.RoleType;
import ru.skillbox.socialnetwork.authentication.entities.user.User;
import ru.skillbox.socialnetwork.authentication.events.ChangedEmailEvent;
import ru.skillbox.socialnetwork.authentication.events.ChangedPasswordEvent;
import ru.skillbox.socialnetwork.authentication.events.NewUserRegisteredEvent;
import ru.skillbox.socialnetwork.authentication.exceptions.EntityNotFoundException;
import ru.skillbox.socialnetwork.authentication.exceptions.RefreshTokenException;
import ru.skillbox.socialnetwork.authentication.mappers.UserMapper;
import ru.skillbox.socialnetwork.authentication.security.AppUserDetails;
import ru.skillbox.socialnetwork.authentication.security.jwt.JwtUtils;
import ru.skillbox.socialnetwork.authentication.repositories.UserRepository;
import ru.skillbox.socialnetwork.authentication.services.PasswordResetTokenService;
import ru.skillbox.socialnetwork.authentication.services.RefreshTokenService;
import ru.skillbox.socialnetwork.authentication.services.SecurityService;
import ru.skillbox.socialnetwork.authentication.web.models.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;

    private final UserRepository userRepository;
    private final KafkaTemplate<String, NewUserRegisteredEvent> kafkaTemplate;

    private final UserMapper userMapper;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.kafka.kafkaUserTopic}")
    private String topicName;

    @Transactional
    @Override
    public void register(CreateUserRequest createUserRequest) {
        var user = User.builder()
                .lastName(createUserRequest.getLastName())
                .firstName(createUserRequest.getFirstName())
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword2()))
                .build();
        Role role = Role.from(RoleType.ROLE_USER, user);
        user.setRoles(new HashSet<>(Set.of(role)));
        userRepository.save(user);
        Optional<User> newUser = userRepository.findByEmail(createUserRequest.getEmail());
        kafkaTemplate.send(topicName, userMapper.userToNewUserRegisteredEvent(newUser.get()));
    }

    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User tokenOwner = userRepository.findById(userId).orElseThrow(() ->
                            new RefreshTokenException("Exception trying to get token for userId: " + userId));
                    String token = jwtUtils.generateTokenFromId(tokenOwner.getId());
                    return new RefreshTokenResponse(token, refreshTokenService.createRefreshToken(userId).getToken());
                }).orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));
    }

    @Override
    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (currentPrincipal instanceof AppUserDetails userDetails) {
            UUID userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
        }
    }

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return LoginResponse.builder()
                .accessToken(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public Boolean validateToken(String token) {
        return jwtUtils.validate(token);
    }

    @Transactional
    @Override
    public Response changePassword(ChangedPasswordEvent event) {
        User user = userRepository.findById(event.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(event.getNewPassword()));
        userRepository.save(user);
        return new Response("Password changed successfully");
    }

    @Transactional
    @Override
    public Response changeEmail(ChangedEmailEvent event) {
        User user = userRepository.findById(event.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setEmail(event.getNewEmail());
        userRepository.save(user);
        return new Response("Email changed successfully");
    }

    @Override
    public Response recoveryPassword(RecoveryPassportRequest request) {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with such email not found"));
        PasswordResetToken resetToken = passwordResetTokenService.createToken(user.getId());
        String resetUrl = String.format("https://185.129.146.54:8765/api/v1/auth/password/reset?token=%s", resetToken.getToken());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject("Смена пароля");

        String text = String.format(
                "Привет, %s %s!\n\nВы запросили восстановление пароля.\n" +
                        "Кликните по ссылке для замены пароля:\n%s\n\n" +
                        "Если вы не запрашивали восстановление — проигнорируйте письмо.",
                user.getFirstName(), user.getLastName(), resetUrl);
        message.setText(text);
        mailSender.send(message);
        return new Response("Email with password has been sent to your email. Please check your inbox.");
    }
}