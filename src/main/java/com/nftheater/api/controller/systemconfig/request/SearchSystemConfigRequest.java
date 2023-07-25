package com.nftheater.api.controller.systemconfig.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchSystemConfigRequest {
    private String configName;
    private LocalDate startCreatedDate;
    private LocalDate endCreatedDate;
    private LocalDate startUpdatedDate;
    private LocalDate endUpdatedDate;
}
