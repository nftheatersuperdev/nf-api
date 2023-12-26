package com.nftheater.api.controller.member.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestOtpRequest {
    @NotNull
    private String mobileNo;
}
