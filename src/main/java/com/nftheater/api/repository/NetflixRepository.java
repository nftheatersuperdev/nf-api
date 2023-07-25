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
}
