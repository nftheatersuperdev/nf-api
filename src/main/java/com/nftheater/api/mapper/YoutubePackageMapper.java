package com.nftheater.api.mapper;

import com.nftheater.api.controller.youtube.response.GetYoutubePackageResponse;
import com.nftheater.api.dto.YoutubePackageDto;
import com.nftheater.api.entity.YoutubePackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface YoutubePackageMapper extends EntityMapper<YoutubePackageDto, YoutubePackageEntity> {

    @Mapping(source = "name", target = "packageName")
    @Mapping(source = "day", target = "packageDay")
    @Mapping(source = "price", target = "packagePrice")
    @Mapping(source = "type", target = "packageType")
    @Mapping(source = "id", target = "packageId")
    GetYoutubePackageResponse toPackageResponse(YoutubePackageDto dto);
}
