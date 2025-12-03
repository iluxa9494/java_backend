package ru.skillbox.socialnetwork.authentication.web.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.authentication.events.ChangedEmailEvent;
import ru.skillbox.socialnetwork.authentication.events.ChangedPasswordEvent;
import ru.skillbox.socialnetwork.authentication.exceptions.AlreadyExitsException;
import ru.skillbox.socialnetwork.authentication.repositories.UserRepository;
import ru.skillbox.socialnetwork.authentication.services.CaptchaService;
import ru.skillbox.socialnetwork.authentication.services.SecurityService;
import ru.skillbox.socialnetwork.authentication.web.models.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    private final SecurityService securityService;
    private final CaptchaService captchaService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public Response registerUser(@RequestBody CreateUserRequest request,
                                      @CookieValue(value = "CAPTCHA_SECRET", required = false) String secret) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExitsException("Email already exists");
        }
        if (!request.getPassword1().equals(request.getPassword2())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (!captchaService.validateCaptcha(secret, request.getCaptchaCode())) {
            throw new RuntimeException("Invalid captcha");
        }
        securityService.register(request);
        return new Response(String.format("User %s %s registered successfully",
                request.getFirstName(), request.getLastName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return securityService.refreshToken(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/password/recovery")
    public Response recoveryPassword(@RequestBody RecoveryPassportRequest request) {
        return securityService.recoveryPassword(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public Response logoutUser() {
        securityService.logout();
        return new Response("Logged out successfully");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponse authUser(@RequestBody LoginRequest loginRequest) {
        return securityService.authenticateUser(loginRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change-password-link")
    public Response changePassword(ChangedPasswordEvent event) {
        return securityService.changePassword(event);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change-email-link")
    public Response changeEmail(ChangedEmailEvent event) {
        return securityService.changeEmail(event);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/validate")
    public Boolean validateToken(@RequestParam String token) {
        return securityService.validateToken(token);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/captcha")
    public CaptchaDto getCaptcha(HttpServletResponse response) {
        CaptchaDto captchaDto = captchaService.generateCaptcha();
        Cookie cookie = new Cookie("CAPTCHA_SECRET", captchaDto.getSecret());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        response.addCookie(cookie);
        return captchaDto;
    }
}