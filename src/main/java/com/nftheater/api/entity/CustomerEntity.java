package com.nftheater.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name = "customerEntity")
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false, length = Integer.MAX_VALUE)
    private String userId;

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "customer_name", length = Integer.MAX_VALUE)
    private String customerName;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "phone_number", length = Integer.MAX_VALUE)
    private String phoneNumber;

    @NotNull
    @Column(name = "line_id", nullable = false, length = Integer.MAX_VALUE)
    private String lineId;

    @Column(name = "line_url", length = Integer.MAX_VALUE)
    private String lineUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "verified_status", length = Integer.MAX_VALUE)
    private String verifiedStatus;

    @Column(name = "customer_status", length = Integer.MAX_VALUE)
    private String customerStatus;

    @Column(name = "expired_date")
    private ZonedDateTime expiredDate;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by", length = Integer.MAX_VALUE)
    private String createdBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "updated_by", length = Integer.MAX_VALUE)
    private String updatedBy;

    @Column(name = "account")
    private String account;

    @OneToMany(mappedBy = "user")
    private List<NetflixAdditionalAccountLinkEntity> netflixAdditionalAccountLinks;

    @OneToMany(mappedBy = "user")
    private List<NetflixAccountLinkEntity> netflixAccountLinks;

    @PrePersist
    public void prePersist() {
        createdDate = ZonedDateTime.now();
        updatedDate = createdDate;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = ZonedDateTime.now();
    }

}