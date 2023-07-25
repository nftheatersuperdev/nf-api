package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixLinkAdditionalEntity;
import com.nftheater.api.entity.NetflixLinkAdditionalEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetflixLinkAdditionalRepository extends JpaRepository<NetflixLinkAdditionalEntity, NetflixLinkAdditionalEntityId> {
}
