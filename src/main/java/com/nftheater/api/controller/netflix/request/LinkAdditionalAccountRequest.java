package com.nftheater.api.controller.netflix.request;

import lombok.Data;

import java.util.UUID;

@Data
public class LinkAdditionalAccountRequest {
    private UUID additionalId;
}
