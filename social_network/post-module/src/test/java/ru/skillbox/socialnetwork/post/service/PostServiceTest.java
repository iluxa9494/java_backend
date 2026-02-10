package ru.skillbox.socialnetwork.post.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.skillbox.socialnetwork.post.dto.post.CreatePostRequest;
import ru.skillbox.socialnetwork.post.entity.PostEntity;
import ru.skillbox.socialnetwork.post.events.PostEvent;
import ru.skillbox.socialnetwork.post.repository.PostRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Test
    void create_ok() {
        // given
        var repo = mock(PostRepository.class);
        var publisher = mock(EventPublisher.class);
        var svc = new PostService(repo, publisher);

        var author = UUID.randomUUID();
        var now = OffsetDateTime.parse("2025-09-22T00:00:00Z");

        // имитируем поведение save() — возвращаем ту же сущность
        when(repo.save(any(PostEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        var dto = svc.create(author, new CreatePostRequest("title", "text"));

        // then: проверяем, что dto корректный
        assertThat(dto.authorId()).isEqualTo(author);
        assertThat(dto.title()).isEqualTo("title");
        assertThat(dto.text()).isEqualTo("text");

        // проверяем, что в репозиторий попала сущность с правильным текстом
        var entityCaptor = ArgumentCaptor.forClass(PostEntity.class);
        verify(repo).save(entityCaptor.capture());
        var saved = entityCaptor.getValue();
        assertThat(saved.getPostText()).isEqualTo("text");
        assertThat(saved.getTitle()).isEqualTo("title");
        assertThat(saved.getAuthorId()).isEqualTo(author);

        // проверяем, что отправилось событие в Kafka через EventPublisher
        var eventCaptor = ArgumentCaptor.forClass(PostEvent.class);
        verify(publisher).publishPost(eventCaptor.capture());
        var event = eventCaptor.getValue();

        // ВАЖНО: имя геттера совпадает с именем поля в record PostEvent.
        // В твоём сервисе создаётся: new PostEvent("CREATED", id, authorId, title, text, likeAmount, commentsCount, time, timeChanged)
        // Значит у record должно быть поле eventType (или type). Используем eventType() как самое вероятное имя.
        assertThat(event.eventType()).isEqualTo("CREATED");
        assertThat(event.id()).isEqualTo(saved.getId());
        assertThat(event.authorId()).isEqualTo(saved.getAuthorId());
        assertThat(event.title()).isEqualTo(saved.getTitle());
        assertThat(event.text()).isEqualTo(saved.getPostText());
        assertThat(event.likeAmount()).isEqualTo(0L);
        assertThat(event.commentsCount()).isEqualTo(0L);
        // time/timeChanged выставляются внутри сервиса — просто проверим, что проставлены
        assertThat(event.time()).isNotNull();
        assertThat(event.timeChanged()).isNotNull();
    }
}