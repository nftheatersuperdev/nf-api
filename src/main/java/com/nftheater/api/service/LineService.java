package com.nftheater.api.service;

import com.nftheater.api.client.LineClient;
import com.nftheater.api.dto.client.line.MessageClientRequest;
import com.nftheater.api.dto.client.line.SendMessageClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class LineService {

    private final LineClient lineClient;

    public void pushMessageToCustomer(String lineUserId, String message) {
        log.info("Line push message to customer : {}", lineUserId);

        SendMessageClientRequest clientRequest = new SendMessageClientRequest();
        MessageClientRequest messageClientRequest = new MessageClientRequest();
        messageClientRequest.setText(message);
        messageClientRequest.setType("text");

        clientRequest.setTo(lineUserId);
        clientRequest.setMessages(Arrays.asList(messageClientRequest));
        try {
            lineClient.sendMessage(clientRequest);
        } catch (Exception ex) {
            log.error("Line push message exception : {}", ex);
        }
    }
}
