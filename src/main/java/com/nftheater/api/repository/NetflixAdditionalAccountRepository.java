package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAdditionalAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NetflixAdditionalAccountRepository extends JpaRepository<NetflixAdditionalAccountEntity, UUID>, JpaSpecificationExecutor<NetflixAdditionalAccountEntity> {
}
