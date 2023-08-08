package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixAdditionalAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NetflixAdditionalAccountRepository extends JpaRepository<NetflixAdditionalAccountEntity, UUID>, JpaSpecificationExecutor<NetflixAdditionalAccountEntity> {

    @Query(value = "SELECT * FROM netflix_additional_account naa " +
            "where not exists (select 1 from netflix_additional_account_link naal " +
            "where naa.id = naal.additional_account_id)"
            , nativeQuery = true)
    List<NetflixAdditionalAccountEntity> getAvailableAccount();

}
