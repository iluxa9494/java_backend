package ru.fastdelivery.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_requests")
@Getter
@Setter
@NoArgsConstructor
public class UserRequestEntity {
    @Id
    private UUID id;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "request_payload")
    private String requestPayload;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "minimal_price")
    private BigDecimal minimalPrice;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "response_payload")
    private String responsePayload;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
