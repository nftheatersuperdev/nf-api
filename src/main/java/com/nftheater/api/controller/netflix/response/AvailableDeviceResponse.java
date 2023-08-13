package com.nftheater.api.controller.netflix.response;

import lombok.Data;

@Data
public class AvailableDeviceResponse {
    private int tvAvailable;
    private int additionalAvailable;
    private int otherAvailable;
}
