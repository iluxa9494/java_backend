package ru.skillbox.socialnetwork.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.post.entity.CommentEntity;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    Page<CommentEntity> findAllByPostIdAndParentIdIsNull(UUID postId, Pageable pageable);

    Page<CommentEntity> findAllByParentId(UUID parentId, Pageable pageable);

    long countByParentId(UUID parentId);
}