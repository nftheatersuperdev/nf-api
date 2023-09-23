package com.nftheater.api.repository;

import com.nftheater.api.entity.NetflixPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NetflixPackageRepository extends JpaRepository<NetflixPackageEntity, UUID> {

    List<NetflixPackageEntity> findByDevice(String device);
    Optional<NetflixPackageEntity> findByName(String name);
}
