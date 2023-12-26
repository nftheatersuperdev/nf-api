package com.nftheater.api.controller.member.request;

import lombok.Data;

@Data
public class VerifyCustomerRequest {

    private String phoneNumber;
    private String lineId;
    private String lineUserId;

}
