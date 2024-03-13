package com.nftheater.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "eventRegisterEntity")
@Table(name = "event_register")
public class EventRegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @NotNull
    @Column(name = "facebook_name")
    private String facebookName;

}
