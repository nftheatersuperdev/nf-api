package com.nftheater.api.controller.youtube.request;

import lombok.Data;

@Data
public class UpdateYoutubeAccountRequest {

    private String changeDate;
    private String password;
}
