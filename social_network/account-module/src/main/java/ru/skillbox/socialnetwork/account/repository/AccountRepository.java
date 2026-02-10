package ru.skillbox.socialnetwork.account.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.account.entity.AccountEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {

    Optional<AccountEntity> findByEmail(String email);
    List<AccountEntity> findByIdIn(List<UUID> ids);

    // Поиск по строке
    @Query("SELECT a FROM AccountEntity a WHERE " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<AccountEntity> findByQuery(@Param("query") String query);

    @Modifying
    @Transactional(transactionManager = "accountTransactionManager")
    @Query("UPDATE AccountEntity a SET a.lastOnlineTime = :time WHERE a.id = :userId AND a.isDeleted = false")
    int updateLastOnlineTime(@Param("userId") UUID userId, @Param("time") LocalDateTime time);

// Поиск по фильтру
//    @Query(value = "SELECT * FROM accounts a WHERE " +
//            "(:ids IS NULL OR a.id IN (:ids)) AND " +
//            "(:author IS NULL OR (a.first_name ILIKE CONCAT('%', :author, '%') OR a.last_name ILIKE CONCAT('%', :author, '%'))) AND " +
//            "(:firstName IS NULL OR a.first_name ILIKE CONCAT('%', :firstName, '%')) AND " +
//            "(:lastName IS NULL OR a.last_name ILIKE CONCAT('%', :lastName, '%')) AND " +
//            "(:city IS NULL OR a.city ILIKE CONCAT('%', :city, '%')) AND " +
//            "(:country IS NULL OR a.country ILIKE CONCAT('%', :country, '%')) AND " +
//            "(:isBlocked IS NULL OR a.is_blocked = :isBlocked) AND " +
//            "(:isDeleted IS NULL OR a.is_deleted = :isDeleted) AND " +
//            "(:birthDateFrom IS NULL OR a.birth_date >= :birthDateFrom) AND " +
//            "(:birthDateTo IS NULL OR a.birth_date <= :birthDateTo)",
//            countQuery = "SELECT count(*) FROM accounts a WHERE " +
//                    "(:ids IS NULL OR a.id IN (:ids)) AND " +
//                    "(:author IS NULL OR (a.first_name ILIKE CONCAT('%', :author, '%') OR a.last_name ILIKE CONCAT('%', :author, '%'))) AND " +
//                    "(:firstName IS NULL OR a.first_name ILIKE CONCAT('%', :firstName, '%')) AND " +
//                    "(:lastName IS NULL OR a.last_name ILIKE CONCAT('%', :lastName, '%')) AND " +
//                    "(:city IS NULL OR a.city ILIKE CONCAT('%', :city, '%')) AND " +
//                    "(:country IS NULL OR a.country ILIKE CONCAT('%', :country, '%')) AND " +
//                    "(:isBlocked IS NULL OR a.is_blocked = :isBlocked) AND " +
//                    "(:isDeleted IS NULL OR a.is_deleted = :isDeleted) AND " +
//                    "(:birthDateFrom IS NULL OR a.birth_date >= :birthDateFrom) AND " +
//                    "(:birthDateTo IS NULL OR a.birth_date <= :birthDateTo)",
//            nativeQuery = true)
//    Page<AccountEntity> searchByFilter(
//            @Param("ids") List<UUID> ids,
//            @Param("author") String author,
//            @Param("firstName") String firstName,
//            @Param("lastName") String lastName,
//            @Param("city") String city,
//            @Param("country") String country,
//            @Param("isBlocked") Boolean isBlocked,
//            @Param("isDeleted") Boolean isDeleted,
//            @Param("birthDateFrom") LocalDateTime birthDateFrom,
//            @Param("birthDateTo") LocalDateTime birthDateTo,
//            Pageable pageable
//    );

    // Получить все ID
    @Query("SELECT a.id FROM AccountEntity a")
    List<UUID> findAllIds();
}
