package com.nftheater.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Table(name = "reward")
@Entity(name = "reward")
public class RewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String rewardName;

    @Column
    private Integer redeemPoint;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column
    private String rewardValue;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column
    private String createdBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column
    private String updatedBy;

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
