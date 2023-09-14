package com.nftheater.api.controller.youtube.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateLinkUserYoutubeRequest {
    private String userId;
    private int extendDay;
    private UUID packageId;
}
