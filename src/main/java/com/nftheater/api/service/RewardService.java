package com.nftheater.api.service;

import com.nftheater.api.controller.reward.request.CreateRewardRequest;
import com.nftheater.api.controller.reward.request.UpdateRewardRequest;
import com.nftheater.api.dto.RewardDto;
import com.nftheater.api.entity.AdminUserEntity;
import com.nftheater.api.entity.RewardEntity;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.mapper.RewardMapper;
import com.nftheater.api.repository.RewardRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {
    private final AdminUserService adminUserService;
    private final RewardRepository rewardRepository;
    private final RewardMapper rewardMapper;

    public List<RewardDto> getAllActiveReward() {
        log.info("Get all active reward");
        List<RewardDto> rewardDtoList = rewardRepository.findByIsActive(true)
                .stream().map(rewardMapper::toDto).toList();
        log.info("Get all active reward size : {}", rewardDtoList.size());
        return rewardDtoList;
    }

    public RewardDto getRewardById(UUID rewardId) throws DataNotFoundException {
        log.info("Get reward with id {}", rewardId.toString());
        RewardDto rewardDto = rewardRepository.findById(rewardId)
                .map(rewardMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบรางวัล {}", rewardId.toString()));
        return rewardDto;
    }

    public UUID createReward(CreateRewardRequest createRewardRequest, UUID adminId) throws DataNotFoundException {
        log.info("Create Reward with {}", createRewardRequest);
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        RewardEntity rewardEntity = rewardMapper.toEntity(createRewardRequest);
        rewardEntity.setIsActive(true);
        rewardEntity.setCreatedBy(adminUser);
        rewardEntity.setUpdatedBy(adminUser);

        rewardRepository.save(rewardEntity);
        return rewardEntity.getId();
    }

    public UUID updateReward(UUID rewardId, UpdateRewardRequest updateRewardRequest, UUID adminId) throws DataNotFoundException {
        log.info("Update Reward with {}", updateRewardRequest);
        final AdminUserEntity adminUserEntity = adminUserService.getAdminUserEntityById(adminId);
        String adminUser = adminUserEntity.getAdminName();

        final RewardEntity rewardEntity = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบรางวัล {}", rewardId.toString()));

        rewardEntity.setRewardName(updateRewardRequest.getRewardName());
        rewardEntity.setRewardValue(updateRewardRequest.getRewardValue());
        rewardEntity.setRedeemPoint(updateRewardRequest.getRedeemPoint());
        rewardEntity.setIsActive(updateRewardRequest.getIsActive());
        rewardEntity.setUpdatedBy(adminUser);

        rewardRepository.save(rewardEntity);
        return rewardEntity.getId();
    }

    public void deleteReward(UUID rewardId) throws DataNotFoundException {
        log.info("Delete reward with id {}", rewardId.toString());

        final RewardEntity rewardEntity = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new DataNotFoundException("ไม่พบรางวัล {}", rewardId.toString()));

        rewardRepository.delete(rewardEntity);
    }

}
