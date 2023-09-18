package com.nftheater.api.repository;

import com.nftheater.api.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID>, JpaSpecificationExecutor<CustomerEntity> {

    Optional<CustomerEntity> findByUserId(String userId);

    @Query(nativeQuery = true, value = "SELECT c.* FROM customer c WHERE c.line_url like %:lineUrl%")
    List<CustomerEntity> findByLineUrl(String lineUrl);

    List<CustomerEntity> findByAccount(String account);

    @Query(value = "SELECT nextval('user_id_seq')", nativeQuery = true)
    Long getUserIdSeq();

}
