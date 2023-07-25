package com.nftheater.api.controller.netflix.request;

import lombok.Data;

@Data
public class SearchNetflixAccountRequest {
    private String userId;
    private String changeDate;
    private String accountName;
    private Boolean isActive;
}
