package com.nftheater.api.controller.netflix.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateLinkUserNetflixRequest {
    private String userId;
    private int extendDay;
    private String accountType;
    private String additionalId;
    private UUID packageId;
}
