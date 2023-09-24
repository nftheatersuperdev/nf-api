package com.nftheater.api.controller.netflix.request;

import lombok.Data;

@Data
public class UpdateAdditionalAccountRequest {
    private String email;
    private String password;
}
