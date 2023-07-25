package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAccountLinkEntity;
import com.nftheater.api.entity.NetflixAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetflixAccountLinkRepository extends JpaRepository<NetflixAccountLinkEntity, NetflixAccountLinkEntityId> {
}
