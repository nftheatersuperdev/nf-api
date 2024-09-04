package com.nftheater.api.client.paysolution;

import com.nftheater.api.client.paysolution.request.CreateSecureLinkRequest;
import com.nftheater.api.client.paysolution.response.CreateSecureLinkResponse;
import com.nftheater.api.config.BusinessConfiguration;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class PaySolutionClient {

    private final RestTemplate restTemplate;
    private final BusinessConfiguration businessConfiguration;

    public ResponseEntity<CreateSecureLinkResponse> createSecureLink(CreateSecureLinkRequest request) {
        final String url = businessConfiguration.getPaySolutionServiceUrl() + businessConfiguration.getPaySolutionMerchantName();
        log.info("Create Secure Link url : {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiKey", businessConfiguration.getPaySolutionApiKey());

        final ResponseEntity<CreateSecureLinkResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {
                }
        );

        log.info("Create Secure Link with status : {}", response.getStatusCode());
        return response;
    }
}
