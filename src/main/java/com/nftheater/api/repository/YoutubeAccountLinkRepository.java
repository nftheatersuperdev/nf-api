package com.nftheater.api.repository;

import com.nftheater.api.entity.YoutubeAccountLinkEntity;
import com.nftheater.api.entity.YoutubeAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface YoutubeAccountLinkRepository extends JpaRepository<YoutubeAccountLinkEntity, YoutubeAccountLinkEntityId>, JpaSpecificationExecutor<YoutubeAccountLinkEntity> {

    Optional<YoutubeAccountLinkEntity> findByUserId(UUID userId);

    List<YoutubeAccountLinkEntity> findByAccountId(UUID accountId);

}
