package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAccountLinkEntity;
import com.nftheater.api.entity.NetflixAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NetflixAccountLinkRepository extends JpaRepository<NetflixAccountLinkEntity, NetflixAccountLinkEntityId> {

    Optional<NetflixAccountLinkEntity> findByUserId(UUID userId);

    List<NetflixAccountLinkEntity> findByAccountId(UUID accountId);
}
