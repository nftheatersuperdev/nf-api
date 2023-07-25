package com.nftheater.api.controller.netflix.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateNetflixAccountRequest {
    private String changeDate;
    private String email;
    private String password;
    private UUID createdBy;

}
