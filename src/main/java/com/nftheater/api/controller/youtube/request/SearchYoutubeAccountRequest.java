package com.nftheater.api.controller.youtube.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchYoutubeAccountRequest {
    private String userId;
    private String changeDate;
    private String accountName;
    private String accountStatus;
    private List<String> customerStatus = new ArrayList<>();
}
