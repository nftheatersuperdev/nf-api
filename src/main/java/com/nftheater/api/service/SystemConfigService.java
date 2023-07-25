package com.nftheater.api.service;

import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.PaginationResponse;
import com.nftheater.api.controller.systemconfig.request.CreateSystemConfigRequest;
import com.nftheater.api.controller.systemconfig.request.SearchSystemConfigRequest;
import com.nftheater.api.controller.systemconfig.request.UpdateSystemConfigRequest;
import com.nftheater.api.controller.systemconfig.response.CreateSystemConfigResponse;
import com.nftheater.api.controller.systemconfig.response.SeachSystemConfigResponse;
import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.controller.systemconfig.response.UpdateSystemConfigResponse;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.dto.SystemConfigDto;
import com.nftheater.api.entity.AdminUserEntity;
import com.nftheater.api.entity.SystemConfigEntity;
import com.nftheater.api.entity.SystemConfigEntity_;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.mapper.SystemConfigMapper;
import com.nftheater.api.repository.SystemConfigRepository;
import com.nftheater.api.utils.DateUtil;
import com.nftheater.api.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.nftheater.api.specification.SystemConfigSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final SystemConfigMapper systemConfigMapper;
    private final Clock clock;
    private final AdminUserService adminUserService;

    @Cacheable(cacheNames = "systemConfigs")
    public List<SystemConfigResponse> getAllConfig() {
        List<SystemConfigEntity> systemConfigEntityList = systemConfigRepository.findAll();

        List<SystemConfigDto> systemConfigDtoList = systemConfigMapper.toDto(systemConfigEntityList);

        return systemConfigMapper.mapDtoToResponses(systemConfigDtoList);
    }

    public SystemConfigResponse getSystemConfigByConfigId(UUID configId) throws DataNotFoundException{
        final SystemConfigEntity systemConfigEntity = systemConfigRepository.findById(configId)
                .orElseThrow(() -> new DataNotFoundException("Config Id " + configId + " is not found."));
        List<AdminUserDto> allAdminUser = adminUserService.getAllAdminUserDto();

        SystemConfigDto systemConfigDto = systemConfigMapper.toDto(systemConfigEntity);
        return systemConfigMapper.toResponse(systemConfigDto);
    }

    public CreateSystemConfigResponse createSystemConfig(CreateSystemConfigRequest createSystemConfigRequest)  throws InvalidRequestException, DataNotFoundException {
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(createSystemConfigRequest.getCreatedBy());
        String createBy = adminUserEntity.getFirstName() + " " + adminUserEntity.getLastName();
        boolean isExistsConfig = systemConfigRepository.existsByConfigName(createSystemConfigRequest.getConfigName());
        if(isExistsConfig) {
            throw new InvalidRequestException("Config " + createSystemConfigRequest.getConfigName() + " is exist in System.");
        }
        SystemConfigEntity newSystemConfig = new SystemConfigEntity();
        newSystemConfig.setConfigName(createSystemConfigRequest.getConfigName());
        newSystemConfig.setConfigValue(createSystemConfigRequest.getConfigValue());
        newSystemConfig.setCreatedBy(createBy);
        newSystemConfig.setUpdatedBy(createBy);

        systemConfigRepository.saveAndFlush(newSystemConfig);

        CreateSystemConfigResponse createSystemConfigResponse = new CreateSystemConfigResponse();
        createSystemConfigResponse.setId(newSystemConfig.getId());

        return createSystemConfigResponse;
    }

    public UpdateSystemConfigResponse updateSystemConfig(UUID configId, UpdateSystemConfigRequest updateSystemConfigRequest) throws DataNotFoundException {
        final SystemConfigEntity systemConfigEntity = systemConfigRepository.findById(configId)
                .orElseThrow(() -> new DataNotFoundException("Config Id " + configId + " is not found."));

        if (updateSystemConfigRequest.getConfigName() != null) {
            systemConfigEntity.setConfigName(updateSystemConfigRequest.getConfigName());
        }
        if (updateSystemConfigRequest.getConfigValue() != null) {
            systemConfigEntity.setConfigValue(updateSystemConfigRequest.getConfigValue());
        }
        systemConfigEntity.setUpdatedBy(updateSystemConfigRequest.getUpdatedBy());
        systemConfigRepository.save(systemConfigEntity);
        UpdateSystemConfigResponse configResponse = new UpdateSystemConfigResponse();
        configResponse.setId(systemConfigEntity.getId());
        return configResponse;
    }

    public void deleteSystemConfig(UUID configId) throws DataNotFoundException {
        final SystemConfigEntity systemConfigEntity = systemConfigRepository.findById(configId)
                .orElseThrow(() -> new DataNotFoundException("Config Id " + configId + " is not found."));

        systemConfigRepository.delete(systemConfigEntity);
    }

    @Transactional(readOnly = true)
    public SeachSystemConfigResponse searchSystemConfig(SearchSystemConfigRequest request, PageableRequest pageableRequest) {
        final Pageable pageable = PageRequest.of(
                pageableRequest.getPageZeroIndex(),
                pageableRequest.getSize(),
                Sort.by(SystemConfigEntity_.CREATED_DATE).descending()
        );

        Specification<SystemConfigEntity> specification = Specification.where(null);
        if (request != null) {
            specification = criteriaConfigNameContain(request, specification);
            specification = criteriaBetweenCreatedDate(request.getStartCreatedDate(), request.getEndCreatedDate(), specification);
            specification = criteriaBetweenUpdatedDate(request.getStartUpdatedDate(), request.getEndUpdatedDate(), specification);
        }
        Page<SystemConfigEntity> systemConfigEntityPage = systemConfigRepository.findAll(specification, pageable);
        Page<SystemConfigDto> systemConfigDtoPage = systemConfigEntityPage.map(systemConfigMapper::toDto);
        List<SystemConfigDto> systemConfigDtoList = systemConfigDtoPage.getContent();

        List<AdminUserDto> allAdminUser = adminUserService.getAllAdminUserDto();

        PaginationResponse pagination = PaginationUtils.createPagination(systemConfigDtoPage);
        SeachSystemConfigResponse response = new SeachSystemConfigResponse();
        response.setPagination(pagination);
        response.setConfig(systemConfigMapper.mapDtoToResponses(systemConfigDtoList));
        return response;
    }

    private Specification<SystemConfigEntity> criteriaBetweenUpdatedDate(LocalDate startUpdatedDate,
                                                                         LocalDate endUpdatedDate,
                                                                         Specification<SystemConfigEntity> specification) {
        if (startUpdatedDate != null && endUpdatedDate != null) {
            ZonedDateTime startUpdatedDateZoned =  ZonedDateTime.of(startUpdatedDate, DateUtil.MIN_TIME, clock.getZone());
            ZonedDateTime endUpdatedDateZoned =  ZonedDateTime.of(startUpdatedDate, DateUtil.MAX_TIME, clock.getZone());
            specification = specification.and(overlapUpdatedDate(startUpdatedDateZoned, endUpdatedDateZoned));
        }
        return specification;
    }

    private Specification<SystemConfigEntity> criteriaBetweenCreatedDate(LocalDate startCreatedDate,
                                                                         LocalDate endCreatedDate,
                                                                         Specification<SystemConfigEntity> specification) {
        if (startCreatedDate != null && endCreatedDate != null) {
            ZonedDateTime startCreatedDateZoned = ZonedDateTime.of(startCreatedDate, DateUtil.MIN_TIME, clock.getZone());
            ZonedDateTime endCreatedDateZoned = ZonedDateTime.of(endCreatedDate, DateUtil.MAX_TIME, clock.getZone());
            specification = specification.and(overlapCreatedDate(startCreatedDateZoned, endCreatedDateZoned));
        }
        return specification;
    }

    private Specification<SystemConfigEntity> criteriaConfigNameContain(SearchSystemConfigRequest criteriaRequest,
                                                                        Specification<SystemConfigEntity> specification) {
        if (criteriaRequest.getConfigName() != null) {
            specification = specification.and(configNameContain(criteriaRequest.getConfigName()));
        }
        return specification;
    }

}
