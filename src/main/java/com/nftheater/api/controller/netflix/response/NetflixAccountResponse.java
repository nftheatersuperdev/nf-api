package com.nftheater.api.controller.netflix.response;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class NetflixAccountResponse {
    private UUID accountId;
    private String accountName;
    private String changeDate;
    private String email;
    private String password;
    private Boolean isActive;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;
    private List<NetflixAdditionalAccountResponse> additionalAccounts = new ArrayList<>();
    private List<NetflixLinkUserResponse> users = new ArrayList<>();
}
