package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixLinkAdditionalEntity;
import com.nftheater.api.entity.NetflixLinkAdditionalEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NetflixLinkAdditionalRepository extends JpaRepository<NetflixLinkAdditionalEntity, NetflixLinkAdditionalEntityId>, JpaSpecificationExecutor<NetflixLinkAdditionalEntity> {

    @Query(value = "select count(*) from netflix_link_additional nla " +
            "join netflix_additional_account_link naal on nla.additional_id = naal.additional_account_id " +
            "join customer c on naal.user_id = c.id " +
            "where nla.account_id = :accountId", nativeQuery = true)
    int getCountUserByAccountId(UUID accountId);

    List<NetflixLinkAdditionalEntity> findByAccountId(UUID accountId);

}
