package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NetflixRepository  extends JpaRepository<NetflixAccountEntity, UUID>, JpaSpecificationExecutor<NetflixAccountEntity> {

    @Query(value = "SELECT nextval('netflix_account_seq')", nativeQuery = true)
    Long getNetflixAccountNameSeq();

    @Query(value = "select count(*) from " +
            "netflix_account na " +
            "join netflix_link_additional nla " +
            "on nla.account_id = na.id " +
            "join netflix_additional_account_link naal " +
            "on naal.additional_account_id = nla.additional_id " +
            "where na.id = :accountId", nativeQuery = true)
    int getAdditionalUserFromAccount(UUID accountId);

    @Query(value = "select count(*) from " +
            "netflix_account na " +
            "join netflix_account_link nal on na.id = nal.account_id " +
            "where na.id = :accountId", nativeQuery = true)
    int getUserFromAccount(UUID accountId);
}
