package com.nftheater.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity(name = "netflixLinkAdditionalEntity")
@Table(name = "netflix_link_additional")
public class NetflixLinkAdditionalEntity {
    @EmbeddedId
    private NetflixLinkAdditionalEntityId id;

    @MapsId("accountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private NetflixAccountEntity account;

    @MapsId("additionalId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "additional_id", nullable = false)
    private NetflixAdditionalAccountEntity additional;

    @Column(name = "added_date")
    private ZonedDateTime addedDate;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        addedDate = ZonedDateTime.now();
        updatedDate = addedDate;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = ZonedDateTime.now();
    }

    public NetflixAdditionalAccountEntity getNetflixAdditionalAccountEntity(){
        return additional;
    }

}