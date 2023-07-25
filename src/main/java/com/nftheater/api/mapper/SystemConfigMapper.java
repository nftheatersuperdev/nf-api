package com.nftheater.api.mapper;

import com.nftheater.api.controller.systemconfig.response.SystemConfigResponse;
import com.nftheater.api.dto.SystemConfigDto;
import com.nftheater.api.entity.SystemConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SystemConfigMapper extends EntityMapper<SystemConfigDto, SystemConfigEntity> {

    SystemConfigResponse toResponse(SystemConfigEntity entity);

    @Mapping(source = "id", target = "configId")
    SystemConfigResponse toResponse(SystemConfigDto dto);

    List<SystemConfigResponse> mapDtoToResponses(List<SystemConfigDto> dtos);
}
