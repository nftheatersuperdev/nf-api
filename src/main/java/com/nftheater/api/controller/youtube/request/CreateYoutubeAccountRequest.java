package com.nftheater.api.controller.youtube.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateYoutubeAccountRequest {
    private String changeDate;
    private String email;
    private String password;
    private UUID createdBy;

}
