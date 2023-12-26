package com.nftheater.api.client;

import com.nftheater.api.config.BusinessConfiguration;
import com.nftheater.api.dto.client.sms.RequestOtpClientResponse;
import com.nftheater.api.dto.client.sms.VerifyOtpClientRequest;
import com.nftheater.api.dto.client.sms.VerifyOtpClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.nftheater.api.constant.ThaiBulkSmsConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThaiBulkSmsClient {

    private final RestTemplate restTemplate;
    private final BusinessConfiguration businessConfiguration;

    public ResponseEntity<VerifyOtpClientResponse> verifyOtp(VerifyOtpClientRequest request) {
        final String url = businessConfiguration.getSmsUrl() + PATH_VERIFY;
        log.info("Verify OTP url : {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(APP_SECRET, businessConfiguration.getSmsAppSecret());
        map.add(APP_KEY, businessConfiguration.getSmsAppKey());
        map.add(REQUEST_TOKEN, request.getToken());
        map.add(PIN_CODE, request.getPinCode());

        final ResponseEntity<VerifyOtpClientResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(map, headers),
                new ParameterizedTypeReference<>() {
                }
        );

        return response;
    }

    public ResponseEntity<RequestOtpClientResponse> requestOtp(String mobile) {
        final String url = businessConfiguration.getSmsUrl() + PATH_REQUEST;
        log.info("Request OTP url : {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(APP_SECRET, businessConfiguration.getSmsAppSecret());
        map.add(APP_KEY, businessConfiguration.getSmsAppKey());
        map.add(MSI_SDN, mobile);

        final ResponseEntity<RequestOtpClientResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(map, headers),
                new ParameterizedTypeReference<>() {
                }
        );

        return response;
    }

}
