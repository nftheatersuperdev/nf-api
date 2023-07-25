package com.nftheater.api.repository;

import com.nftheater.api.entity.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity, UUID>, JpaSpecificationExecutor<SystemConfigEntity> {

    Optional<SystemConfigEntity> findByConfigName(String configName);

    boolean existsByConfigName(String configName);

}
