package com.nftheater.api.dto.client.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestOtpClientResponse {
    private String status;
    private String token;
    @JsonProperty(value = "refno")
    private String refNo;
}
