package com.nftheater.api.controller.netflix.response;

import lombok.Data;

import java.util.UUID;

@Data
public class GetAvailableAdditionAccountResponse {

    private UUID additionalId;
    private String email;

}
