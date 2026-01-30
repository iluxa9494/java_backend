package ru.skillbox.socialnetwork.post.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import ru.skillbox.socialnetwork.post.dto.filter.PostFilter;
import ru.skillbox.socialnetwork.post.entity.PostEntity;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class PostSpecs {
    private PostSpecs() {}

    public static Specification<PostEntity> withFilter(PostFilter f) {
        return (root, cq, cb) -> {
            if (f == null) return cb.conjunction();

            List<Predicate> p = new ArrayList<>();

            if (f.ids() != null && !f.ids().isEmpty()) {
                p.add(root.get("id").in(f.ids()));
            }
            if (f.authorIds() != null && !f.authorIds().isEmpty()) {
                p.add(root.get("authorId").in(f.authorIds()));
            }
            if (f.q() != null && !f.q().isBlank()) {
                String like = "%" + f.q().toLowerCase() + "%";
                p.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("postText")), like) // поле в Entity называется postText
                ));
            }
            if (f.dateFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("time"), f.dateFrom()));
            }
            if (f.dateTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("time"), f.dateTo()));
            }
            if (f.isDeleted() != null) {
                p.add(cb.equal(root.get("isDeleted"), f.isDeleted()));
            }
            if (f.isBlocked() != null) {
                p.add(cb.equal(root.get("isBlocked"), f.isBlocked()));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}