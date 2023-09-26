package com.nftheater.api.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class NetflixAccountDto {

    private UUID id;
    private String accountName;
    private String changeDate;
    private String billDate;
    private String netflixEmail;
    private String netflixPassword;
    private Boolean isActive;
    private ZonedDateTime expiredDate;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;
    private List<NetflixAdditionalAccountDto> additionalAccounts;
    private List<NetflixLinkUserDto> accountLinks;
}
