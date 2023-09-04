package com.nftheater.api.controller.netflix.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchNetflixAccountRequest {
    private String userId;
    private String changeDate;
    private String accountName;
    private Boolean isActive;
    private List<String> customerStatus = new ArrayList<>();
    private Boolean filterTVAvailable;
    private Boolean filterOtherAvailable;
    private Boolean filterAdditionalAvailable;
}
