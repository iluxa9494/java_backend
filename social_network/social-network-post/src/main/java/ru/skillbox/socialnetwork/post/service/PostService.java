package ru.skillbox.socialnetwork.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.post.dto.filter.PostFilter;
import ru.skillbox.socialnetwork.post.dto.post.PagePostDto;
import ru.skillbox.socialnetwork.post.dto.post.CreatePostRequest;
import ru.skillbox.socialnetwork.post.dto.post.PostDto;
import ru.skillbox.socialnetwork.post.dto.post.UpdatePostRequest;
import ru.skillbox.socialnetwork.post.entity.PostEntity;
import ru.skillbox.socialnetwork.post.events.PostEvent;
import ru.skillbox.socialnetwork.post.mapper.PostMapper;
import ru.skillbox.socialnetwork.post.repository.PostRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    /* ---------- CREATE ---------- */

    @Transactional
    public PostDto create(UUID authorId, String title, String text) {
        PostEntity e = new PostEntity();
        e.setId(UUID.randomUUID());
        e.setAuthorId(authorId);
        e.setTitle(title);
        e.setPostText(text);
        e.setLikeAmount(0L);
        e.setCommentsCount(0L);
        e.setBlocked(false);
        e.setDeleted(false);
        e.setTime(OffsetDateTime.now());
        e.setTimeChanged(e.getTime());

        PostEntity saved = postRepository.save(e);

        eventPublisher.publishPost(new PostEvent(
                "CREATED",
                saved.getId(),
                saved.getAuthorId(),
                saved.getTitle(),
                saved.getPostText(),
                nz(saved.getLikeAmount()),
                nz(saved.getCommentsCount()),
                saved.getTime(),
                saved.getTimeChanged()
        ));
        return PostMapper.toDto(saved);
    }

    // перегрузка под контроллер
    @Transactional
    public PostDto create(UUID authorId, CreatePostRequest req) {
        return create(authorId, req.title(), req.text());
    }

    /* ---------- READ ---------- */
    @Transactional(readOnly = true)
    public PostDto get(UUID postId) {
        PostEntity e = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        return PostMapper.toDto(e);
    }

    /* ---------- UPDATE ---------- */

    @Transactional
    public PostDto update(UUID postId, String title, String text) {
        PostEntity e = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        e.setTitle(title);
        e.setPostText(text);
        e.setTimeChanged(OffsetDateTime.now());
        PostEntity saved = postRepository.save(e);

        eventPublisher.publishPost(new PostEvent(
                "UPDATED",
                saved.getId(),
                saved.getAuthorId(),
                saved.getTitle(),
                saved.getPostText(),
                nz(saved.getLikeAmount()),
                nz(saved.getCommentsCount()),
                saved.getTime(),
                saved.getTimeChanged()
        ));
        return PostMapper.toDto(saved);
    }

    // перегрузка под контроллер
    @Transactional
    public PostDto update(UUID postId, UpdatePostRequest req) {
        return update(postId, req.title(), req.text());
    }

    /* ---------- DELETE / RESTORE ---------- */

    @Transactional
    public void delete(UUID postId) {
        PostEntity e = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        e.setDeleted(true);
        e.setTimeChanged(OffsetDateTime.now());
        PostEntity saved = postRepository.save(e);

        eventPublisher.publishPost(new PostEvent(
                "DELETED",
                saved.getId(),
                saved.getAuthorId(),
                saved.getTitle(),
                saved.getPostText(),
                nz(saved.getLikeAmount()),
                nz(saved.getCommentsCount()),
                saved.getTime(),
                saved.getTimeChanged()
        ));
    }

    // чтобы не менять контроллер — оставим "softDelete" как алиас
    @Transactional
    public void softDelete(UUID postId) {
        delete(postId);
    }

    @Transactional
    public void restore(UUID postId) {
        PostEntity e = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        e.setDeleted(false);
        e.setTimeChanged(OffsetDateTime.now());
        PostEntity saved = postRepository.save(e);

        eventPublisher.publishPost(new PostEvent(
                "RESTORED",
                saved.getId(),
                saved.getAuthorId(),
                saved.getTitle(),
                saved.getPostText(),
                nz(saved.getLikeAmount()),
                nz(saved.getCommentsCount()),
                saved.getTime(),
                saved.getTimeChanged()
        ));
    }

    /* ---------- SEARCH (минимальная реализация под сигнатуру контроллера) ---------- */

    @Transactional(readOnly = true)
    public PagePostDto search(PostFilter filter, PageRequest pageRequest) {
        // Простейшая версия: без фильтров, только пагинация (чтобы компилировалось и работало).
        // Если у тебя в репозитории есть метод поиска по фильтрам — подставь его здесь.
        Page<PostEntity> page = postRepository.findAll(pageRequest);

        List<PostDto> content = page.getContent().stream()
                .map(PostMapper::toDto)
                .toList();

        return new PagePostDto(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private static long nz(Long v) {
        return v == null ? 0L : v;
    }
}