package com.nftheater.api.controller.netflix.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNetflixAccountResponse {

    private UUID id;
    private String accountName;
}
