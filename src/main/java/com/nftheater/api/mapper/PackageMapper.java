package com.nftheater.api.mapper;

import com.nftheater.api.controller.packages.request.UpdatePackageRequest;
import com.nftheater.api.dto.PackageDto;
import com.nftheater.api.entity.NetflixPackageEntity;
import com.nftheater.api.entity.YoutubePackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PackageMapper {

    PackageDto toPackageDto(NetflixPackageEntity entity);

    PackageDto toPackageDto(YoutubePackageEntity entity);

}
