package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAdditionalAccountLinkEntity;
import com.nftheater.api.entity.NetflixAdditionalAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NetflixAdditionalAccountLinkRepository extends JpaRepository<NetflixAdditionalAccountLinkEntity, NetflixAdditionalAccountLinkEntityId> {

    Optional<NetflixAdditionalAccountLinkEntity> findByUserId(UUID userId);
}
