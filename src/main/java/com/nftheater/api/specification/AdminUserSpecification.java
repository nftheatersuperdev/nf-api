package com.nftheater.api.specification;

import com.nftheater.api.entity.AdminUserEntity;
import com.nftheater.api.entity.AdminUserEntity_;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class AdminUserSpecification {

    private static final String PERCENT_SIGN = "%";

    private AdminUserSpecification() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static Specification<AdminUserEntity> idEqual(UUID adminId) {
        return (root, cq, cb) -> cb.equal(root.get(AdminUserEntity_.id), adminId);
    }

    public static Specification<AdminUserEntity> nameContain(String firstName) {
        return (root, cq, cb) -> cb.like(root.get(AdminUserEntity_.adminName), PERCENT_SIGN + firstName + PERCENT_SIGN);
    }

    public static Specification<AdminUserEntity> emailContain(String email) {
        return (root, cq, cb) -> cb.like(root.get(AdminUserEntity_.email), PERCENT_SIGN + email + PERCENT_SIGN);
    }

    public static Specification<AdminUserEntity>moduleEqual(String module) {
        return (root, cq, cb) -> cb.equal(root.get(AdminUserEntity_.module), module);
    }

    public static Specification<AdminUserEntity> roleEqual(String role) {
        return (root, cq, cb) -> cb.equal(root.get(AdminUserEntity_.role), role);
    }

    public static Specification<AdminUserEntity> activeStatusEqual(Boolean isActive) {
        return (root, cq, cb) -> cb.equal(root.get(AdminUserEntity_.isActive), isActive);
    }

}
