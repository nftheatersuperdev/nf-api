package com.nftheater.api.controller.youtube.request;

import lombok.Data;

@Data
public class UpdateLinkUserYoutubeRequest {
    private String userId;
    private int extendDay;
}
