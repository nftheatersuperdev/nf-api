package com.nftheater.api.controller.youtube.response;

import lombok.Data;

@Data
public class GetYoutubePackageResponse {
    private String packageName;
    private int packageDay;
    private int packagePrice;
    private String packageType;
}
