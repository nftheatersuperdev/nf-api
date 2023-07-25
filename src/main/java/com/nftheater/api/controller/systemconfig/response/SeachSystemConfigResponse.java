package com.nftheater.api.controller.systemconfig.response;

import com.nftheater.api.controller.response.PaginationResponse;
import lombok.Data;

import java.util.List;

@Data
public class SeachSystemConfigResponse {
    private PaginationResponse pagination;
    private List<SystemConfigResponse> config;
}
