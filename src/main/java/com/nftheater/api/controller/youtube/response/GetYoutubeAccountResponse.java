package com.nftheater.api.controller.youtube.response;

import lombok.Data;

import java.util.UUID;

@Data
public class GetYoutubeAccountResponse {

    private UUID accountId;
    private String accountName;

}
