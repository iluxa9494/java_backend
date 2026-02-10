package ru.skillbox.socialnetwork.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.post.dto.comment.CommentDto;
import ru.skillbox.socialnetwork.post.dto.comment.PageCommentDto;
import ru.skillbox.socialnetwork.post.service.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "COMMENTS", description = "Operations with comments and replies")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    // ---------- DTOs ----------
    public record CreateCommentRequest(
            @Schema(description = "Text of comment", example = "Nice post!")
            @NotBlank(message = "text must not be blank")
            String text,

            @Schema(description = "Parent comment ID for reply", format = "uuid",
                    example = "00000000-0000-0000-0000-000000000222")
            UUID parentId
    ) {}

    public record UpdateCommentRequest(
            @Schema(description = "New text of comment", example = "Updated text")
            @NotBlank(message = "text must not be blank")
            String text
    ) {}

    // ---------- Helpers ----------
    private PageCommentDto toPageDto(Page<CommentDto> p) {
        List<CommentDto> content = p.getContent();
        return new PageCommentDto(
                content,
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }

    // ---------- Endpoints ----------

    @Operation(summary = "Get top-level comments of the post (paged)")
    @GetMapping
    public ResponseEntity<PageCommentDto> getComments(
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("postId") UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentDto> res = service.getCommentsByPost(postId, PageRequest.of(page, size));
        return ResponseEntity.ok(toPageDto(res));
    }

    @Operation(summary = "Get replies for a comment (paged)")
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<PageCommentDto> getReplies(
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("postId") UUID postId,
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("commentId") UUID commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentDto> res = service.getSubComments(commentId, PageRequest.of(page, size));
        return ResponseEntity.ok(toPageDto(res));
    }

    @Operation(summary = "Create comment or reply")
    @PostMapping
    public ResponseEntity<CommentDto> create(
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("postId") UUID postId,
            @Valid @RequestBody CreateCommentRequest rq
    ) {
        CommentDto dto = service.create(postId, rq.parentId(), rq.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Update comment text")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("postId") UUID postId,
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("commentId") UUID commentId,
            @Valid @RequestBody UpdateCommentRequest rq
    ) {
        CommentDto dto = service.update(commentId, rq.text());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Delete (soft) comment")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("postId") UUID postId,
            @Parameter(schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("commentId") UUID commentId
    ) {
        service.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}