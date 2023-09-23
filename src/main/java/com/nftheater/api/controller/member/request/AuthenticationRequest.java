package com.nftheater.api.controller.member.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
