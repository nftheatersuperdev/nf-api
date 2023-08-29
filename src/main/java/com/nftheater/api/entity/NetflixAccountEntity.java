package com.nftheater.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name = "netflixAccountEntity")
@Table(name = "netflix_account")
public class NetflixAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "change_date")
    private String changeDate;

    @Column(name = "netflix_email", unique=true)
    private String netflixEmail;

    @Column(name = "netflix_password")
    private String netflixPassword;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "account")
    private List<NetflixAccountLinkEntity> accountLinks;

    @OneToMany(mappedBy = "account")
    private List<NetflixLinkAdditionalEntity> additionalAccounts;

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