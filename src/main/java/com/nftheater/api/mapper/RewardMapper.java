package com.nftheater.api.mapper;

import com.nftheater.api.controller.reward.request.CreateRewardRequest;
import com.nftheater.api.dto.RewardDto;
import com.nftheater.api.entity.RewardEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RewardMapper  extends EntityMapper<RewardDto, RewardEntity> {

    RewardEntity toEntity(CreateRewardRequest request);
}
