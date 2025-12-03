package ru.skillbox.socialnetwork.dialog.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.dtos.MessageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    Integer unreadCount;
    UUID conversationPartner1;
    UUID conversationPartner2;

    @OneToOne
    @JoinColumn(name = "last_message_id")
    Message lastMessage;

    @OneToMany(mappedBy = "dialog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
}
