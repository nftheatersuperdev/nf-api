package com.nftheater.api.specification;

import com.nftheater.api.entity.SystemConfigEntity;
import com.nftheater.api.entity.SystemConfigEntity_;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class SystemConfigSpecification {

    private static final String PERCENT_SIGN = "%";

    private SystemConfigSpecification() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static Specification<SystemConfigEntity> configNameContain(String configName) {
        return (root, cq, cb) -> cb.like(root.get(SystemConfigEntity_.CONFIG_NAME), PERCENT_SIGN + configName + PERCENT_SIGN);
    }

    public static Specification<SystemConfigEntity> overlapCreatedDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (systemConfigEntity, cq, cb) ->
                cb.and(
                        cb.lessThanOrEqualTo(systemConfigEntity.get(SystemConfigEntity_.CREATED_DATE), endDate),
                        cb.greaterThanOrEqualTo(systemConfigEntity.get(SystemConfigEntity_.CREATED_DATE), startDate)
                );
    }

    public static Specification<SystemConfigEntity> overlapUpdatedDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (systemConfigEntity, cq, cb) ->
                cb.and(
                        cb.lessThanOrEqualTo(systemConfigEntity.get(SystemConfigEntity_.UPDATED_DATE), endDate),
                        cb.greaterThanOrEqualTo(systemConfigEntity.get(SystemConfigEntity_.UPDATED_DATE), startDate)
                );
    }
}
