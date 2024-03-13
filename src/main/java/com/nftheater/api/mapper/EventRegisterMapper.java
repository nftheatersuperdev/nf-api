package com.nftheater.api.mapper;

import com.nftheater.api.dto.EventRegisterDto;
import com.nftheater.api.entity.EventRegisterEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventRegisterMapper extends EntityMapper<EventRegisterDto, EventRegisterEntity> {
}
