package com.nftheater.api.controller.systemconfig.response;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class SystemConfigResponse {

    private UUID configId;
    private String configName;
    private String configValue;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;

}
