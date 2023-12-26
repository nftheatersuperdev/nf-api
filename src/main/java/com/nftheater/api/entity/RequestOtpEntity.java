package com.nftheater.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity(name = "requestOtpEntity")
@Table(name = "request_otp")
public class RequestOtpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "retryCount", nullable = false)
    private Integer retryCount;

    @Column(name = "phone_number", length = Integer.MAX_VALUE)
    private String phoneNumber;

    @Column(name = "request_token", nullable = false)
    private String requestToken;

    @Column(name = "ref_no", nullable = false)
    private String refNo;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "message")
    private String message;

    @Column(name = "requested_date")
    private ZonedDateTime requestedDate;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        requestedDate = ZonedDateTime.now();
        updatedDate = requestedDate;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = ZonedDateTime.now();
    }

}
