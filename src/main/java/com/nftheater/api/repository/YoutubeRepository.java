package com.nftheater.api.repository;

import com.nftheater.api.entity.YoutubeAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface YoutubeRepository extends JpaRepository<YoutubeAccountEntity, UUID>, JpaSpecificationExecutor<YoutubeAccountEntity> {

    @Query(value = "SELECT nextval('youtube_account_seq')", nativeQuery = true)
    Long getYoutubeAccountNameSeq();
}
