package com.nftheater.api.controller.systemconfig;

import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.controller.systemconfig.request.CreateSystemConfigRequest;
import com.nftheater.api.controller.systemconfig.request.SearchSystemConfigRequest;
import com.nftheater.api.controller.systemconfig.request.UpdateSystemConfigRequest;
import com.nftheater.api.controller.systemconfig.response.CreateSystemConfigResponse;
import com.nftheater.api.controller.systemconfig.response.SeachSystemConfigResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.controller.systemconfig.response.UpdateSystemConfigResponse;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/v1/system-configs")
    public GeneralResponse<List<SystemConfigResponse>> getAllSystemConfig() {
        log.info("Start Get All System Config");
        List<SystemConfigResponse> allConfigs = systemConfigService.getAllConfig();
        log.info("End Get All System Config size = {}", allConfigs.size());
        return new GeneralResponse<>(SUCCESS, allConfigs);
    }

    @GetMapping("/v1/system-config/{configId}")
    public GeneralResponse<SystemConfigResponse> getSystemConfigById(@PathVariable("configId") UUID configId) throws DataNotFoundException {
        log.info("Start Get System Config by Id {}", configId);
        SystemConfigResponse configResponse = systemConfigService.getSystemConfigByConfigId(configId);
        log.info("End Get System Config by Id {}", configId);
        return new GeneralResponse<>(SUCCESS, configResponse);
    }
    
    @PostMapping("/v1/system-configs")
    public GeneralResponse<CreateSystemConfigResponse> createConfig(@RequestBody CreateSystemConfigRequest request) throws InvalidRequestException, DataNotFoundException {
        log.info("Start Create config with request : {}", request);
        CreateSystemConfigResponse response = systemConfigService.createSystemConfig(request);
        log.info("End Create config with config id  : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PostMapping("/v1/system-config/search")
    public GeneralResponse<SeachSystemConfigResponse> searchAdminUsers(
            @RequestBody(required = false) SearchSystemConfigRequest searchSystemConfigRequest,
            PageableRequest pageableRequest
    ) {
        log.info("Start Search config");
        SeachSystemConfigResponse response = systemConfigService.searchSystemConfig(searchSystemConfigRequest, pageableRequest);
        log.info("End Search config size : {}", response.getConfig().size());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PatchMapping("/v1/system-config/{configId}")
    public GeneralResponse<UpdateSystemConfigResponse> updateConfig(@PathVariable("configId") UUID configId,
                                                                    @RequestBody UpdateSystemConfigRequest request)
            throws DataNotFoundException {
        log.info("Start Update config with id : {} and request : {}", configId, request);
        UpdateSystemConfigResponse response = systemConfigService.updateSystemConfig(configId, request);
        log.info("End Update config with id : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @DeleteMapping("/v1/system-config/{configId}")
    public GeneralResponse<Void> deleteConfig(@PathVariable("configId") UUID config) throws DataNotFoundException {
        log.info("Start Delete config with id : {}",config);
        systemConfigService.deleteSystemConfig(config);
        log.info("End Delete config with id : {}",config);
        return new GeneralResponse<>(SUCCESS, null);
    }
}
