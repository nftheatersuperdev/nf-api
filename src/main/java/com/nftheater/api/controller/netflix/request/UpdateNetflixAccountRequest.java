package com.nftheater.api.controller.netflix.request;

import lombok.Data;

@Data
public class UpdateNetflixAccountRequest {
    private String changeDate;
    private String email;
    private String password;
}
