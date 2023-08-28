package com.nftheater.api.controller.adminuser.request;

import com.nftheater.api.constant.Module;
import lombok.Data;

@Data
public class CreateAdminUserRequest {
    private String firebaseToken;
    private String adminName;
    private String role;
}
