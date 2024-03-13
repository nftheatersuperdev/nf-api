package com.nftheater.api.repository;

import com.nftheater.api.entity.EventRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRegisterRepository extends JpaRepository<EventRegisterEntity, UUID> {

    Optional<EventRegisterEntity> findByUserIdAndFacebookName(String userId, String facebook);

}