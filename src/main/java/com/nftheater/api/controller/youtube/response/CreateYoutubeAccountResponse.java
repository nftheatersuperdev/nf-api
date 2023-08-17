package com.nftheater.api.controller.youtube.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateYoutubeAccountResponse {

    private UUID id;
    private String accountName;
}
