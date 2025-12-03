package ru.skillbox.socialnetwork.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.post.dto.filter.PostFilter;
import ru.skillbox.socialnetwork.post.dto.post.CreatePostRequest;
import ru.skillbox.socialnetwork.post.dto.post.PagePostDto;
import ru.skillbox.socialnetwork.post.dto.post.PostDto;
import ru.skillbox.socialnetwork.post.dto.post.UpdatePostRequest;
import ru.skillbox.socialnetwork.post.service.PostService;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping
    public PostDto create(@RequestBody @Valid CreatePostRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID authorId)) {
            return null;
        }
        return service.create(authorId, req);
    }

    @GetMapping("/{id}")
    public PostDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public PostDto update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePostRequest req
    ) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable UUID id) {
        service.softDelete(id);
    }

    @PostMapping("/{id}/restore")
    public void restore(@PathVariable UUID id) {
        service.restore(id);
    }

    // --------- поиск/листинг ----------
    @GetMapping("/s")
    public PagePostDto search(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "ids", required = false) String idsCsv,
            @RequestParam(name = "authorIds", required = false) String authorIdsCsv,
            @RequestParam(name = "dateFrom", required = false) String dateFromIso,
            @RequestParam(name = "dateTo", required = false) String dateToIso,
            @RequestParam(name = "deleted", required = false) Boolean deleted,
            @RequestParam(name = "blocked", required = false) Boolean blocked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time,desc") String sort // формат: field,dir
    ) {
        Set<UUID> ids = parseUuidCsv(idsCsv);
        Set<UUID> authorIds = parseUuidCsv(authorIdsCsv);
        OffsetDateTime dateFrom = parseOdt(dateFromIso);
        OffsetDateTime dateTo = parseOdt(dateToIso);

        PostFilter filter = new PostFilter(
                ids,
                authorIds,
                q,          // имя поля в record — q
                dateFrom,
                dateTo,
                deleted,    // isDeleted
                blocked     // isBlocked
        );

        String[] parts = sort.split(",", 2);
        Sort.Direction dir = (parts.length == 2 && parts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortSpec = Sort.by(dir, parts[0]);

        PageRequest pr = PageRequest.of(page, size, sortSpec);
        return service.search(filter, pr);
    }

    // ---------- helpers ----------
    private static Set<UUID> parseUuidCsv(String csv) {
        if (csv == null || csv.isBlank()) return null;
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    private static OffsetDateTime parseOdt(String iso) {
        if (iso == null || iso.isBlank()) return null;
        return OffsetDateTime.parse(iso);
    }
}