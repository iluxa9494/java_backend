package ru.skillbox.socialnetwork.dialog.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.enums.ReadStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "dialog_id", nullable = false)
    private Dialog dialog;

    LocalDateTime time;
    String messageText;
    UUID conversationPartner1;
    UUID conversationPartner2;

    @Enumerated(EnumType.STRING)
    ReadStatus readStatus;

    Boolean stubDate;
    String date;
}
