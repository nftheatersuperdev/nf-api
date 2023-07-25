package com.nftheater.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity(name = "netflixAdditionalAccountLinkEntity")
@Table(name = "netflix_additional_account_link")
public class NetflixAdditionalAccountLinkEntity {
    @EmbeddedId
    private NetflixAdditionalAccountLinkEntityId id;

    @MapsId("additionalAccountId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "additional_account_id", nullable = false)
    private NetflixAdditionalAccountEntity additionalAccount;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private CustomerEntity user;

    @Column(name = "added_date")
    private ZonedDateTime addedDate;

    @Column(name = "added_by")
    private String addedBy;

}