package com.team58.mc_notifications.kafka.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Событие лайка поста или комментария.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LikeEvent extends BaseNotificationEvent {

    private UUID postId;    // лайк к посту
    private UUID commentId; // лайк к комменту
}