package com.nftheater.api.mapper;

import com.nftheater.api.controller.netflix.response.GetNetflixPackageResponse;
import com.nftheater.api.dto.NetflixPackageDto;
import com.nftheater.api.entity.NetflixPackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NetflixPackageMapper extends EntityMapper<NetflixPackageDto, NetflixPackageEntity> {

    @Mapping(source = "name", target = "packageName")
    @Mapping(source = "day", target = "packageDay")
    @Mapping(source = "price", target = "packagePrice")
    GetNetflixPackageResponse toPackageResponse(NetflixPackageDto dto);
}
