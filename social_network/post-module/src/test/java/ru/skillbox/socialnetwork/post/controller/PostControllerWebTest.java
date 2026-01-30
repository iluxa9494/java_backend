package ru.skillbox.socialnetwork.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//noinspection removal
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnetwork.post.dto.post.CreatePostRequest;
import ru.skillbox.socialnetwork.post.dto.post.PostDto;
import ru.skillbox.socialnetwork.post.dto.post.UpdatePostRequest;
import ru.skillbox.socialnetwork.post.service.PostService;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerWebTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @SuppressWarnings("removal")
    @MockBean PostService service;

    private static final UUID POST_ID   = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID AUTHOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");

    @Test
    void create_shouldReturn200WithBody() throws Exception {
        CreatePostRequest rq = new CreatePostRequest("Hello", "World");

        PostDto dto = new PostDto(
                POST_ID,
                AUTHOR_ID,
                "Hello",
                "World",
                0L,                      // likeAmount
                0L,                      // commentsCount
                OffsetDateTime.parse("2025-09-22T00:00:00Z"),
                OffsetDateTime.parse("2025-09-22T00:00:00Z"),
                false,
                false
        );

        when(service.create(eq(AUTHOR_ID), any(CreatePostRequest.class))).thenReturn(dto);

        mvc.perform(post("/api/v1/post")
                        .header("X-Author-Id", AUTHOR_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(rq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(POST_ID.toString()))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID.toString()))
                .andExpect(jsonPath("$.title").value("Hello"))
                .andExpect(jsonPath("$.text").value("World"))
                .andExpect(jsonPath("$.likeAmount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    void get_shouldReturnPost() throws Exception {
        PostDto dto = new PostDto(
                POST_ID,
                AUTHOR_ID,
                "T",
                "B",
                0L,
                0L,
                OffsetDateTime.parse("2025-09-22T00:00:00Z"),
                OffsetDateTime.parse("2025-09-22T00:00:00Z"),
                false,
                false
        );
        when(service.get(POST_ID)).thenReturn(dto);

        mvc.perform(get("/api/v1/post/{id}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(POST_ID.toString()))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID.toString()))
                .andExpect(jsonPath("$.likeAmount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    void update_shouldReturnUpdatedPost() throws Exception {
        UpdatePostRequest rq = new UpdatePostRequest("new", "body");

        PostDto dto = new PostDto(
                POST_ID,
                AUTHOR_ID,
                "new",
                "body",
                0L,
                0L,
                OffsetDateTime.parse("2025-09-22T00:00:00Z"),
                OffsetDateTime.parse("2025-09-22T01:00:00Z"),
                false,
                false
        );
        when(service.update(eq(POST_ID), any(UpdatePostRequest.class))).thenReturn(dto);

        mvc.perform(put("/api/v1/post/{id}", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(rq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new"))
                .andExpect(jsonPath("$.text").value("body"))
                .andExpect(jsonPath("$.likeAmount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }
}