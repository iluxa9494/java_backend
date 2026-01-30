package ru.skillbox.socialnetwork.dialog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.socialnetwork.dialog.enums.ReadStatus;
import ru.skillbox.socialnetwork.dialog.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {


    @Query("""
            SELECT m FROM Message m
            WHERE (m.conversationPartner1 = :partner1 AND m.conversationPartner2 = :partner2)
               OR (m.conversationPartner1 = :partner2 AND m.conversationPartner2 = :partner1)
            """)
    Page<Message> findAllByConversationPartners(
            @Param("partner1") UUID partner1,
            @Param("partner2") UUID partner2,
            Pageable pageable
    );


    @Query("""
            SELECT m FROM Message m
            WHERE m.dialog.id = :dialogId AND m.readStatus = :readStatus
            """)
    List<Message> findMessagesByDialogIdAndStatus(
            @Param("dialogId") UUID dialogId,
            @Param("readStatus") ReadStatus readStatus);
}
