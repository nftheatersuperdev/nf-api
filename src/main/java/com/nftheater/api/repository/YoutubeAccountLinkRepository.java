package com.nftheater.api.repository;

import com.nftheater.api.entity.YoutubeAccountLinkEntity;
import com.nftheater.api.entity.YoutubeAccountLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface YoutubeAccountLinkRepository extends JpaRepository<YoutubeAccountLinkEntity, YoutubeAccountLinkEntityId> {

    Optional<YoutubeAccountLinkEntity> findByUserId(UUID userId);

    List<YoutubeAccountLinkEntity> findByAccountId(UUID accountId);

}
