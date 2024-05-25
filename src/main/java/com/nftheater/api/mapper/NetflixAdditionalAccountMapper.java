package com.nftheater.api.mapper;

import com.nftheater.api.dto.NetflixAdditionalAccountDto;
import com.nftheater.api.entity.NetflixAdditionalAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NetflixAdditionalAccountMapper {

    @Mapping(target = "additionalId", source = "id")
    @Mapping(target = "email", source = "additionalEmail")
    @Mapping(target = "password", source = "additionalPassword")
    @Mapping(target = "user", source = "netflixAdditionalAccountLink.user")
    @Mapping(target = "netflixAccount", source = "netflixLinkAdditionals.account")
    NetflixAdditionalAccountDto toDto(NetflixAdditionalAccountEntity entity);

}
