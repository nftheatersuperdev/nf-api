package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAccountLinkEntity;
import com.nftheater.api.entity.NetflixAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NetflixAccountLinkRepository extends JpaRepository<NetflixAccountLinkEntity, NetflixAccountLinkEntityId> , JpaSpecificationExecutor<NetflixAccountLinkEntity> {

    Optional<NetflixAccountLinkEntity> findByUserId(UUID userId);

    List<NetflixAccountLinkEntity> findByAccountId(UUID accountId);
}
