package ru.skillbox.socialnetwork.account.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.account.dto.AccountDto;
import ru.skillbox.socialnetwork.account.exception.ValidationException;
import ru.skillbox.socialnetwork.account.repository.AccountRepository;

import java.util.regex.Pattern;

@Component
@Slf4j
public class AccountValidator {

    private final AccountRepository accountRepository;

    // Регулярные выражения для валидации
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");

    public AccountValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * Валидация данных аккаунта при создании
     */
    public void validateCreate(AccountDto dto) {
        log.info("🔍 Валидация данных для создания аккаунта: {}", dto.getEmail());

        validateEmail(dto.getEmail());
        validateFirstName(dto.getFirstName());
        validateLastName(dto.getLastName());

        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            validatePhone(dto.getPhone());
        }

        checkEmailUnique(dto.getEmail());

        log.info("✅ Валидация пройдена успешно");
    }

    /**
     * Валидация данных аккаунта при обновлении
     */
    public void validateUpdate(AccountDto dto, String currentEmail) {
        log.info("🔍 Валидация данных для обновления аккаунта: {}", currentEmail);

        if (dto.getEmail() != null && !dto.getEmail().equals(currentEmail)) {
            validateEmail(dto.getEmail());
            checkEmailUnique(dto.getEmail());
        }

        if (dto.getFirstName() != null) {
            validateFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            validateLastName(dto.getLastName());
        }

        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            validatePhone(dto.getPhone());
        }

        log.info("✅ Валидация обновления пройдена успешно");
    }

    /**
     * Валидация email
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email не может быть пустым", "email", "EMAIL_EMPTY");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Некорректный формат email", "email", "EMAIL_INVALID");
        }

        if (email.length() > 255) {
            throw new ValidationException("Email слишком длинный", "email", "EMAIL_TOO_LONG");
        }
    }

    /**
     * Валидация пароля
     */
    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Пароль не может быть пустым", "password", "PASSWORD_EMPTY");
        }

        if (password.length() < 8) {
            throw new ValidationException("Пароль должен содержать минимум 8 символов", "password", "PASSWORD_TOO_SHORT");
        }

        if (password.length() > 100) {
            throw new ValidationException("Пароль слишком длинный", "password", "PASSWORD_TOO_LONG");
        }

        // Проверка на сложность пароля
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Пароль должен содержать хотя бы одну заглавную букву", "password", "PASSWORD_NO_UPPERCASE");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Пароль должен содержать хотя бы одну строчную букву", "password", "PASSWORD_NO_LOWERCASE");
        }

        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Пароль должен содержать хотя бы одну цифру", "password", "PASSWORD_NO_DIGIT");
        }
    }

    /**
     * Валидация имени
     */
    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым", "firstName", "FIRST_NAME_EMPTY");
        }

        if (!NAME_PATTERN.matcher(firstName).matches()) {
            throw new ValidationException("Имя должно содержать от 2 до 50 символов (только буквы, пробелы и дефисы)", "firstName", "FIRST_NAME_INVALID");
        }
    }

    /**
     * Валидация фамилии
     */
    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Фамилия не может быть пустым", "lastName", "LAST_NAME_EMPTY");
        }

        if (!NAME_PATTERN.matcher(lastName).matches()) {
            throw new ValidationException("Фамилия должна содержать от 2 до 50 символов (только буквы, пробелы и дефисы)", "lastName", "LAST_NAME_INVALID");
        }
    }

    /**
     * Валидация телефона
     */
    private void validatePhone(String phone) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Некорректный формат телефона", "phone", "PHONE_INVALID");
        }
    }

    /**
     * Проверка уникальности email
     */
    private void checkEmailUnique(String email) {
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("Email уже используется", "email", "EMAIL_ALREADY_EXISTS");
        }
    }
}