package com.team58.mc_notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Ответ на обновление настроек уведомлений.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsUpdateResponse {

    /**
     * Время формирования ответа (UTC).
     */
    private OffsetDateTime time;

    /**
     * "Уведомления о личных сообщениях".
     */
    private boolean messageEnabled;
}