package ru.skillbox.socialnetwork.authentication.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.socialnetwork.authentication.repositories.UserRepository;
import ru.skillbox.socialnetwork.authentication.services.CaptchaService;
import ru.skillbox.socialnetwork.authentication.services.SecurityService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private CaptchaService captchaService;

    @Test
    void register_returnsCreated_forValidPayload() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(captchaService.validateCaptcha("secret", "1234")).thenReturn(true);

        String payload = """
                {
                  "email": "user@example.com",
                  "password1": "password123",
                  "password2": "password123",
                  "firstName": "Ivan",
                  "lastName": "Petrov",
                  "captchaCode": "1234"
                }
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/register")
                                .cookie(new jakarta.servlet.http.Cookie("CAPTCHA_SECRET", "secret"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void register_returnsBadRequest_whenPasswordMissing() throws Exception {
        String payload = """
                {
                  "email": "user@example.com",
                  "password2": "password123",
                  "firstName": "Ivan",
                  "lastName": "Petrov",
                  "captchaCode": "1234"
                }
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("password1")));
    }

    @Test
    void register_acceptsFormUrlEncodedPayload() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(captchaService.validateCaptcha("secret", "1234")).thenReturn(true);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/register")
                                .cookie(new jakarta.servlet.http.Cookie("CAPTCHA_SECRET", "secret"))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("email", "user@example.com")
                                .param("password1", "password123")
                                .param("password2", "password123")
                                .param("firstName", "Ivan")
                                .param("lastName", "Petrov")
                                .param("captchaCode", "1234")
                )
                .andExpect(status().isCreated());
    }
}
