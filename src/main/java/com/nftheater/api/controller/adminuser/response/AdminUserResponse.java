package com.nftheater.api.controller.adminuser.response;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class AdminUserResponse {
    private UUID id;
    private String firebaseId;
    private String email;
    private String adminName;
    private String role;
    private String account;
    private Boolean isActive;
    private ZonedDateTime createdDate;
    private ZonedDateTime updatedDate;
}
