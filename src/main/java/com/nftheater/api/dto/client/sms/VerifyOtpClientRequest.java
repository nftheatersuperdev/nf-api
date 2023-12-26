package com.nftheater.api.dto.client.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpClientRequest {
    private String pinCode;
    private String token;
}
