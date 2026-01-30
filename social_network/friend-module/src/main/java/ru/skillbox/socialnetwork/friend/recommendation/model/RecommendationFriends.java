package ru.skillbox.socialnetwork.friend.recommendation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "recommendation_friends")
@Getter
@Setter
public class RecommendationFriends {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String photo;

    private String firstName;

    private String lastName;

}
