package com.nftheater.api.controller.netflix.response;

import lombok.Data;

@Data
public class GetNetflixPackageResponse {
    private String packageName;
    private int packageDay;
    private int packagePrice;
}
