package com.nftheater.api.controller.member.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotNull
    private String pinCode;

    @NotNull
    private String refCode;

}
