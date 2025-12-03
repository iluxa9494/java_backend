package ru.skillbox.socialnetwork.dialog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.socialnetwork.dialog.model.Dialog;

import java.util.Optional;
import java.util.UUID;

public interface DialogRepository extends JpaRepository<Dialog, UUID> {


    @Query("""
            SELECT d FROM Dialog d
            WHERE d.conversationPartner1 = :userId
               OR d.conversationPartner2 = :userId
            """)
    Page<Dialog> findAllByUserId(
            @Param("userId") UUID userId,
            Pageable pageable
    );


    @Query("""
            SELECT d FROM Dialog d
            WHERE (d.conversationPartner1 = :partner1 AND d.conversationPartner2 = :partner2)
               OR (d.conversationPartner1 = :partner2 AND d.conversationPartner2 = :partner1)
            """)
    Optional<Dialog> findByConversationPartners(
            @Param("partner1") UUID partner1,
            @Param("partner2") UUID partner2
    );


    @Query("""
            SELECT COUNT(d) FROM Dialog d
            WHERE (d.conversationPartner1 = :userId OR d.conversationPartner2 = :userId)
               AND d.unreadCount > :unreadCount
            """)
    Long countDialogsByUserIdAndUnreadCountGreaterThan(
            @Param("userId") UUID userId,
            @Param("unreadCount" ) int unreadCount
    );
}
