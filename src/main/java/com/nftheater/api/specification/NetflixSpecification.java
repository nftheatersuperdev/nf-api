package com.nftheater.api.specification;

import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.entity.*;
import com.nftheater.api.utils.SqlUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.List;

public class NetflixSpecification {

    private NetflixSpecification() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static Specification<NetflixAccountEntity> changeDateEqual(String changeDate) {
        return (root, cq, cb) -> cb.equal(root.get(NetflixAccountEntity_.CHANGE_DATE), changeDate);
    }

    public static Specification<NetflixAccountEntity> billDateEqual(String billDate) {
        return (root, cq, cb) -> cb.equal(root.get(NetflixAccountEntity_.BILL_DATE), billDate);
    }

    public static Specification<NetflixAccountEntity> userIdContain(String userId) {
        return (netflixAccountEntity, cq, cb) -> {
                Join<NetflixAccountEntity, NetflixAccountLinkEntity> accountJoinAccountLink = netflixAccountEntity.join(NetflixAccountEntity_.ACCOUNT_LINKS, JoinType.LEFT);
                Join<NetflixAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer = accountJoinAccountLink.join(NetflixAccountLinkEntity_.USER, JoinType.LEFT);
        return cb.like(accountLinkJoinCustomer.get(CustomerEntity_.USER_ID), BusinessConstants.PERCENT_SIGN + userId + BusinessConstants.PERCENT_SIGN);
        };
    }

    public static Specification<NetflixAccountEntity> addUserIdContain(String userId) {
        return (netflixAccountEntity, cq, cb) -> {
            Join<NetflixAccountEntity, NetflixLinkAdditionalEntity> accountJoinAdditionalLink =
                    netflixAccountEntity.join(NetflixAccountEntity_.ADDITIONAL_ACCOUNTS, JoinType.LEFT);
            Join<NetflixLinkAdditionalEntity, NetflixAdditionalAccountEntity> linkAdditionalJoinAdditional =
                    accountJoinAdditionalLink.join(NetflixLinkAdditionalEntity_.ADDITIONAL, JoinType.LEFT);
            Join<NetflixAdditionalAccountEntity, NetflixAdditionalAccountLinkEntity> accountJoinAccountLink =
                    linkAdditionalJoinAdditional.join(NetflixAdditionalAccountEntity_.NETFLIX_ADDITIONAL_ACCOUNT_LINK, JoinType.LEFT);
            Join<NetflixAdditionalAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer =
                    accountJoinAccountLink.join(NetflixAdditionalAccountLinkEntity_.USER, JoinType.LEFT);
            return cb.like(accountLinkJoinCustomer.get(CustomerEntity_.USER_ID), BusinessConstants.PERCENT_SIGN + userId + BusinessConstants.PERCENT_SIGN);
        };
    }

    public static Specification<NetflixAccountEntity> isActiveEqual(Boolean isActive) {
        return (netflixAccountEntity, cq, cb) -> cb.equal(netflixAccountEntity.get(NetflixAccountEntity_.IS_ACTIVE), isActive);
    }

    public static Specification<NetflixAccountEntity> accountNameEqual(String accountName) {
        return (netflixAccountEntity, cq, cb) -> cb.equal(netflixAccountEntity.get(NetflixAccountEntity_.ACCOUNT_NAME), accountName);
    }

    public static Specification<NetflixAccountEntity> accountEmailContain(String accountEmail) {
        return (netflixAccountEntity, cq, cb) -> cb.like(netflixAccountEntity.get(NetflixAccountEntity_.NETFLIX_EMAIL), "%" + SqlUtil.escape(accountEmail) + "%");
    }

    public static Specification<NetflixAccountEntity> customerStatusIn(List<String> customerStatus) {
        return (netflixAccountEntity, cq, cb) -> {
            Join<NetflixAccountEntity, NetflixAccountLinkEntity> accountJoinAccountLink = netflixAccountEntity.join(NetflixAccountEntity_.ACCOUNT_LINKS, JoinType.LEFT);
            Join<NetflixAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer = accountJoinAccountLink.join(NetflixAccountLinkEntity_.USER, JoinType.LEFT);
            return cb.in(accountLinkJoinCustomer.get(CustomerEntity_.CUSTOMER_STATUS)).value(customerStatus);
        };
    }

    public static Specification<NetflixAccountLinkEntity> overlapAddedDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (netflixAccountLinkEntity, cq, cb) ->
                cb.and(
                        cb.lessThanOrEqualTo(netflixAccountLinkEntity.get(NetflixAccountLinkEntity_.ADDED_DATE), endDate),
                        cb.greaterThanOrEqualTo(netflixAccountLinkEntity.get(NetflixAccountLinkEntity_.ADDED_DATE), startDate)
                );
    }

    public static Specification<NetflixLinkAdditionalEntity> overlapAdditionalAddedDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (root, cq, cb) ->
                cb.and(
                        cb.lessThanOrEqualTo(root.get(NetflixLinkAdditionalEntity_.ADDED_DATE), endDate),
                        cb.greaterThanOrEqualTo(root.get(NetflixLinkAdditionalEntity_.ADDED_DATE), startDate)
                );
    }
}

