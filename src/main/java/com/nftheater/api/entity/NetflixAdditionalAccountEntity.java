package com.nftheater.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name = "netflixAdditionalAccountEntity")
@Table(name = "netflix_additional_account")
public class NetflixAdditionalAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "additional_email", length = Integer.MAX_VALUE)
    private String additionalEmail;

    @Column(name = "additional_password", length = Integer.MAX_VALUE)
    private String additionalPassword;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToOne(mappedBy = "additionalAccount")
    private NetflixAdditionalAccountLinkEntity netflixAdditionalAccountLink;

    @OneToOne(mappedBy = "additional")
    private NetflixLinkAdditionalEntity netflixLinkAdditionals;

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