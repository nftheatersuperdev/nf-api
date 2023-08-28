package com.nftheater.api.controller.adminuser.request;

import lombok.Data;

@Data
public class SearchAdminUserRequest {
    private String email;
    private String adminName;
    private String module;
    private String role;
    private Boolean isActive;
}
