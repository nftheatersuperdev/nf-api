package com.nftheater.api.service;

import com.nftheater.api.client.ThaiBulkSmsClient;
import com.nftheater.api.dto.client.sms.RequestOtpClientResponse;
import com.nftheater.api.dto.client.sms.VerifyOtpClientRequest;
import com.nftheater.api.dto.client.sms.VerifyOtpClientResponse;
import com.nftheater.api.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    private final ThaiBulkSmsClient thaiBulkSmsClient;

    public VerifyOtpClientResponse verifyOtp(String token, String pinCode) {
        log.info("Verify Otp for token : {}", token);

        ResponseEntity<VerifyOtpClientResponse> responseEntity = thaiBulkSmsClient.verifyOtp(new VerifyOtpClientRequest(pinCode, token));

        log.info("Verify Otp with Response Code : {}", responseEntity.getStatusCode());
        VerifyOtpClientResponse clientResponse = responseEntity.getBody();

        return  clientResponse;
    }

    public RequestOtpClientResponse requestOtp(String mobile) throws InvalidRequestException {
        log.info("Request Otp for mobile : {}", mobile);

        ResponseEntity<RequestOtpClientResponse> responseEntity = thaiBulkSmsClient.requestOtp(mobile);


        log.info("Request Otp with Response Code : {}", responseEntity.getStatusCode());

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new InvalidRequestException("Cannot request otp for mobile : " + mobile + ", Please try again.");
        }
    }
}
