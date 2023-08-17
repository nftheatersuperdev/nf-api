package com.nftheater.api.repository;

import com.nftheater.api.entity.YoutubePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface YoutubePackageRepository extends JpaRepository<YoutubePackageEntity, UUID> {

    List<YoutubePackageEntity> findByType(String type);
}
