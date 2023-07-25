package com.nftheater.api.controller.adminuser.request;

import com.nftheater.api.constant.ModuleEnum;
import lombok.Data;

@Data
public class CreateAdminUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private ModuleEnum module;
    private String role;
}
