package com.nftheater.api.controller.netflix.request;

import lombok.Data;

@Data
public class UpdateLinkUserNetflixRequest {
    private String userId;
    private int extendDay;
    private String accountType;
}
