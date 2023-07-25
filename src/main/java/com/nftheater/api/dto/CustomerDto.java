package com.nftheater.api.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class CustomerDto {

    private UUID id;
    private String userId;
    private String password;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String lineId;
    private String lineUrl;
    private Boolean isActive;
    private String verifiedStatus;
    private String customerStatus;
    private ZonedDateTime expiredDate;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;

}
