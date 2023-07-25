package com.nftheater.api.repository;

import com.nftheater.api.entity.AdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUserEntity, UUID>, JpaSpecificationExecutor<AdminUserEntity> {

    Optional<AdminUserEntity> findByFirebaseId(String firebaseId);
}
