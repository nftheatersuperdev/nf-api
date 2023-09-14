package com.nftheater.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity(name = "netflixAccountLinkEntity")
@Table(name = "netflix_account_link")
public class NetflixAccountLinkEntity {
    @EmbeddedId
    private NetflixAccountLinkEntityId id;

    @MapsId("accountId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private NetflixAccountEntity account;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private CustomerEntity user;

    @Column(name = "account_type", length = Integer.MAX_VALUE)
    private String accountType;

    @Column(name = "added_date")
    private ZonedDateTime addedDate;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "package_name")
    private String packageName;

}