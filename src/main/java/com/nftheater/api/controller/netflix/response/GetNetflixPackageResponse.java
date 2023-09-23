package com.nftheater.api.controller.netflix.response;

import lombok.Data;

import java.util.UUID;

@Data
public class GetNetflixPackageResponse {
    private UUID packageId;
    private String packageName;
    private int packageDay;
    private int packagePrice;
    private String device;
}
