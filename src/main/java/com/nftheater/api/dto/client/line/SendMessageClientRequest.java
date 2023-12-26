package com.nftheater.api.dto.client.line;

import lombok.Data;

import java.util.List;

@Data
public class SendMessageClientRequest {

    public String to;
    public List<MessageClientRequest> messages;

}
