package com.nftheater.api.controller.youtube.response;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class YoutubeAccountResponse {

    private UUID accountId;
    private String accountName;
    private String changeDate;
    private String email;
    private String password;
    private String accountStatus;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;
    private List<YoutubeLinkUserResponse> users = new ArrayList<>();
    private int availableDevice;
}
