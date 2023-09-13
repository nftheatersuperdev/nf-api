package com.nftheater.api.controller.youtube.response;

import lombok.Data;

import java.util.UUID;

@Data
public class GetYoutubePackageResponse {
    private UUID packageId;
    private String packageName;
    private int packageDay;
    private int packagePrice;
    private String packageType;
}
