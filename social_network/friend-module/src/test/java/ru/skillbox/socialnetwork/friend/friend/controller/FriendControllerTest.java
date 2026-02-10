package ru.skillbox.socialnetwork.friend.friend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendsSearchRequest;
import ru.skillbox.socialnetwork.friend.friend.service.FriendshipService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendshipService friendshipService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(userId, "n/a", "ROLE_USER"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findPageFriends_bindsQueryParamsIntoRequest() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(friendshipService.findPageFriends(eq(userId), any(FriendsSearchRequest.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/friends")
                        .param("ids", id1.toString())
                        .param("ids", id2.toString())
                        .param("firstName", "Ann")
                        .param("birthDateFrom", "1990-01-01")
                        .param("birthDateTo", "2000-12-31")
                        .param("city", "Moscow")
                        .param("country", "RU")
                        .param("ageFrom", "18")
                        .param("ageTo", "30")
                        .param("statusCode", "FRIEND"))
                .andExpect(status().isOk());

        ArgumentCaptor<FriendsSearchRequest> captor = ArgumentCaptor.forClass(FriendsSearchRequest.class);
        verify(friendshipService).findPageFriends(eq(userId), captor.capture(), any(Pageable.class));

        FriendsSearchRequest request = captor.getValue();
        assertThat(request.getIds()).containsExactly(id1, id2);
        assertThat(request.getFirstName()).isEqualTo("Ann");
        assertThat(request.getBirthDateFrom()).isEqualTo(java.time.LocalDate.parse("1990-01-01"));
        assertThat(request.getBirthDateTo()).isEqualTo(java.time.LocalDate.parse("2000-12-31"));
        assertThat(request.getCity()).isEqualTo("Moscow");
        assertThat(request.getCountry()).isEqualTo("RU");
        assertThat(request.getAgeFrom()).isEqualTo(18);
        assertThat(request.getAgeTo()).isEqualTo(30);
        assertThat(request.getStatusCode()).isEqualTo("FRIEND");
    }
}
