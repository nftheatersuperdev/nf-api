package com.nftheater.api.controller.adminuser.request;

import com.nftheater.api.constant.ModuleEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class SearchAdminUserRequest {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private ModuleEnum module;
    private String role;
    private boolean isActive;
}
