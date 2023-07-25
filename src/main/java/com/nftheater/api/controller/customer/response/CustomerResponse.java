package com.nftheater.api.controller.customer.response;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CustomerResponse {
    private String userId;
    private String password;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String lineId;
    private String lineUrl;
    private String verifiedStatus;
    private String customerStatus;
    private ZonedDateTime expiredDate;
    private long dayLeft;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;
}
