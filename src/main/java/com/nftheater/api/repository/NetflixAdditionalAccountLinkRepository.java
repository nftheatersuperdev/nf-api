package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAdditionalAccountLinkEntity;
import com.nftheater.api.entity.NetflixAdditionalAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NetflixAdditionalAccountLinkRepository extends JpaRepository<NetflixAdditionalAccountLinkEntity, NetflixAdditionalAccountLinkEntityId> {

    Optional<NetflixAdditionalAccountLinkEntity> findByUserId(UUID userId);

    Optional<NetflixAdditionalAccountLinkEntity> findByAdditionalAccountId(UUID additionalAccountId);
}
