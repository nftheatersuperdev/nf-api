package com.nftheater.api.controller.customerweb.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
