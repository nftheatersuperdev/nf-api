package com.nftheater.api.specification;

import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.nftheater.api.specification.JoinEntitySpecification.joinList;

public class NetflixSpecification {

    private NetflixSpecification() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static Specification<NetflixAccountEntity> changeDateEqual(String changeDate) {
        return (root, cq, cb) -> cb.equal(root.get(NetflixAccountEntity_.CHANGE_DATE), changeDate);
    }

    public static Specification<NetflixAccountEntity> userIdContain(String userId) {
        return (netflixAccountEntity, cq, cb) -> {
                Join<NetflixAccountEntity, NetflixAccountLinkEntity> accountJoinAccountLink = netflixAccountEntity.join(NetflixAccountEntity_.ACCOUNT_LINKS, JoinType.INNER);
                Join<NetflixAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer = accountJoinAccountLink.join(NetflixAccountLinkEntity_.USER, JoinType.INNER);
        return cb.like(accountLinkJoinCustomer.get(CustomerEntity_.USER_ID), BusinessConstants.PERCENT_SIGN + userId + BusinessConstants.PERCENT_SIGN);
        };
    }

    public static Specification<NetflixAccountEntity> isActiveEqual(Boolean isActive) {
        return (netflixAccountEntity, cq, cb) -> cb.equal(netflixAccountEntity.get(NetflixAccountEntity_.IS_ACTIVE), isActive);
    }

    public static Specification<NetflixAccountEntity> accountNameContain(String accountName) {
        return (netflixAccountEntity, cq, cb) -> cb.like(netflixAccountEntity.get(NetflixAccountEntity_.ACCOUNT_NAME), BusinessConstants.PERCENT_SIGN + accountName + BusinessConstants.PERCENT_SIGN);
    }

    public static Specification<NetflixAccountEntity> customerStatusIn(List<String> customerStatus) {
        return (netflixAccountEntity, cq, cb) -> {
            Join<NetflixAccountEntity, NetflixAccountLinkEntity> accountJoinAccountLink = netflixAccountEntity.join(NetflixAccountEntity_.ACCOUNT_LINKS, JoinType.INNER);
            Join<NetflixAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer = accountJoinAccountLink.join(NetflixAccountLinkEntity_.USER, JoinType.INNER);
            return cb.in(accountLinkJoinCustomer.get(CustomerEntity_.CUSTOMER_STATUS)).value(customerStatus);
        };
    }
}

