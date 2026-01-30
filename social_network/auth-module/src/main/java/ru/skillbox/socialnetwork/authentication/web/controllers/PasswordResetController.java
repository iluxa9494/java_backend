package ru.skillbox.socialnetwork.authentication.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.socialnetwork.authentication.entities.user.User;
import ru.skillbox.socialnetwork.authentication.exceptions.EntityNotFoundException;
import ru.skillbox.socialnetwork.authentication.repositories.UserRepository;
import ru.skillbox.socialnetwork.authentication.services.PasswordResetTokenService;

@Controller
@RequestMapping("/api/v1/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetTokenService tokenService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/reset")
    public String showResetPage(@RequestParam("token") String token, Model model) {
        var tokenOpt = tokenService.validateToken(token);
        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Неверный или просроченный токен");
            return "error";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset/submit")
    public String handleReset(
            @RequestParam("token") String token,
            @RequestParam("password") String newPassword,
            Model model) {

        var tokenOpt = tokenService.validateToken(token);
        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Неверный или просроченный токен");
            return "error";
        }

        var tokenEntity = tokenOpt.get();
        User user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenService.deleteToken(token);

        model.addAttribute("message", "Ваш пароль успешно изменён!");
        return "success";
    }
}