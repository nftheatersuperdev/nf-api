package com.nftheater.api.controller.adminuser.request;

import lombok.Data;

@Data
public class CreateAdminUserRequest {
    private String firebaseToken;
    private String adminName;
    private String role;
}
