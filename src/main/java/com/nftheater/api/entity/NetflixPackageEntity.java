package com.nftheater.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "netflix_package")
public class NetflixPackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "day", nullable = false)
    private Integer day;

    @NotNull
    @Column(name = "price", nullable = false, length = Integer.MAX_VALUE)
    private String price;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by", length = Integer.MAX_VALUE)
    private String createdBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "updated_by", length = Integer.MAX_VALUE)
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