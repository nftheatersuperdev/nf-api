package com.nftheater.api.dto.client.sms;

import lombok.Data;

import java.util.List;

@Data
public class VerifyOtpClientResponse {

    private String status;
    private String message;
    private Integer code;
    private List<ErrorClientResponse> errors;

}
