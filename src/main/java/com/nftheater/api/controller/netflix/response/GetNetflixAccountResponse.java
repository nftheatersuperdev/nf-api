package com.nftheater.api.controller.netflix.response;

import lombok.Data;

import java.util.UUID;

@Data
public class GetNetflixAccountResponse {

    private UUID accountId;
    private String accountName;

}
