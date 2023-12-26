package com.nftheater.api.repository;

import com.nftheater.api.entity.RequestOtpEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestOtpRepository extends CrudRepository<RequestOtpEntity, UUID> {

    Optional<RequestOtpEntity> findByUserId(String userId);

    Optional<RequestOtpEntity> findByUserIdAndRefNo(String userId, String refNo);

}
