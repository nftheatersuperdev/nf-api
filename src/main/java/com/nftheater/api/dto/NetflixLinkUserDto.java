package com.nftheater.api.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class NetflixLinkUserDto {
    private CustomerDto user;
    private String accountType;
    private ZonedDateTime addedDate;
    private String addedBy;
    private String packageName;
}
