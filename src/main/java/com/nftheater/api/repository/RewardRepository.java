package com.nftheater.api.repository;

import com.nftheater.api.entity.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardRepository extends JpaRepository<RewardEntity, UUID> {

    List<RewardEntity> findByIsActive(Boolean isActive);
}
