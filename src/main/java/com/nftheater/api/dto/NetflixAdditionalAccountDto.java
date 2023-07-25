package com.nftheater.api.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class NetflixAdditionalAccountDto {
    private String email;
    private String password;
    private ZonedDateTime addedDate;
    private String addedBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;
    private CustomerDto user;
}
