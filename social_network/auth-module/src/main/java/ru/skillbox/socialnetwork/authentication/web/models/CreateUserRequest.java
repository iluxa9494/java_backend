package ru.skillbox.socialnetwork.authentication.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest {

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, max = 20)
    @NotBlank
    private String password1;

    @Size(min = 8, max = 20)
    @NotBlank
    private String password2;

    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё]{2,30}([ -][A-Za-zА-Яа-яЁё]{2,30})*$")
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё]{2,30}([ -][A-Za-zА-Яа-яЁё]{2,30})*$")
    private String firstName;

    @NotBlank
    private String captchaCode;

    @AssertTrue(message = "Passwords do not match")
    public boolean isEqualPassword() {
        return Objects.equals(password1, password2);
    }
}
