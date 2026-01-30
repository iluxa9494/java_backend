package ru.skillbox.socialnetwork.account.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.skillbox.socialnetwork.account.entity.AccountEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AccountSpecifications {

    public static Specification<AccountEntity> withIds(List<UUID> ids) {
        return (root, query, criteriaBuilder) ->
                ids != null && !ids.isEmpty() ?
                        root.get("id").in(ids) : null;
    }

    public static Specification<AccountEntity> withAuthor(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.trim().isEmpty()) return null;
            String pattern = "%" + author.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern)
            );
        };
    }

    public static Specification<AccountEntity> withFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                firstName != null && !firstName.trim().isEmpty() ?
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%") : null;
    }

    public static Specification<AccountEntity> withLastName(String lastName) {
        return (root, query, criteriaBuilder) ->
                lastName != null && !lastName.trim().isEmpty() ?
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%") : null;
    }

    public static Specification<AccountEntity> withCity(String city) {
        return (root, query, criteriaBuilder) ->
                city != null && !city.trim().isEmpty() ?
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%") : null;
    }

    public static Specification<AccountEntity> withCountry(String country) {
        return (root, query, criteriaBuilder) ->
                country != null && !country.trim().isEmpty() ?
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + country.toLowerCase() + "%") : null;
    }

    public static Specification<AccountEntity> withIsBlocked(Boolean isBlocked) {
        return (root, query, criteriaBuilder) ->
                isBlocked != null ?
                        criteriaBuilder.equal(root.get("isBlocked"), isBlocked) : null;
    }

    public static Specification<AccountEntity> withIsDeleted(Boolean isDeleted) {
        return (root, query, criteriaBuilder) ->
                isDeleted != null ?
                        criteriaBuilder.equal(root.get("isDeleted"), isDeleted) : null;
    }

    public static Specification<AccountEntity> withBirthDateFrom(LocalDate birthDateFrom) {
        return (root, query, criteriaBuilder) ->
                birthDateFrom != null ?
                        criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), birthDateFrom) : null;
    }

    public static Specification<AccountEntity> withBirthDateTo(LocalDate birthDateTo) {
        return (root, query, criteriaBuilder) ->
                birthDateTo != null ?
                        criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), birthDateTo) : null;
    }

    public static Specification<AccountEntity> withAgeFrom(Integer ageFrom) {
        return (root, query, criteriaBuilder) -> {
            if (ageFrom == null) return null;
            LocalDate maxBirthDate = LocalDate.now().minusYears(ageFrom);
            return criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), maxBirthDate);
        };
    }

    public static Specification<AccountEntity> withAgeTo(Integer ageTo) {
        return (root, query, criteriaBuilder) -> {
            if (ageTo == null) return null;
            LocalDate minBirthDate = LocalDate.now().minusYears(ageTo + 1).plusDays(1);
            return criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), minBirthDate);
        };
    }
}