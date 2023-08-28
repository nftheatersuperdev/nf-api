package com.nftheater.api.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class AdminUserDto {
    private UUID id;
    private String firebaseId;
    private String email;
    private String adminName;
    private String role;
    private String module;
    private Boolean isActive;
    private ZonedDateTime createdDate;
    private ZonedDateTime updatedDate;
}
