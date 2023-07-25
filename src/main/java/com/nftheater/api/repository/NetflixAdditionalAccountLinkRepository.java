package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAdditionalAccountLinkEntity;
import com.nftheater.api.entity.NetflixAdditionalAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetflixAdditionalAccountLinkRepository extends JpaRepository<NetflixAdditionalAccountLinkEntity, NetflixAdditionalAccountLinkEntityId> {
}
