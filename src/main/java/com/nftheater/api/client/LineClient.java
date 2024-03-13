package com.nftheater.api.client;

import com.nftheater.api.config.BusinessConfiguration;
import com.nftheater.api.dto.client.line.SendMessageClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class LineClient {

    private final RestTemplate restTemplate;
    private final BusinessConfiguration businessConfiguration;

    public void sendMessage(SendMessageClientRequest sendMessageClientRequest) {
        final String url = businessConfiguration.getLineUrl() + "/push";
        log.info("Line send message url : {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(businessConfiguration.getLineToken());

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(sendMessageClientRequest, headers),
                new ParameterizedTypeReference<>() {
                }
        );
    }

}
