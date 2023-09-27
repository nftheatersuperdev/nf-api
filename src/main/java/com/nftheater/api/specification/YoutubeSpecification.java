package com.nftheater.api.specification;

import com.nftheater.api.constant.BusinessConstants;
import com.nftheater.api.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.List;

public class YoutubeSpecification {

    private YoutubeSpecification()  {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static Specification<YoutubeAccountEntity> changeDateEqual(String changeDate) {
        return (root, cq, cb) -> cb.equal(root.get(YoutubeAccountEntity_.CHANGE_DATE), changeDate);
    }

    public static Specification<YoutubeAccountEntity> billDateEqual(String billDate) {
        return (root, cq, cb) -> cb.equal(root.get(YoutubeAccountEntity_.BILL_DATE), billDate);
    }

    public static Specification<YoutubeAccountEntity> userIdContain(String userId) {
        return (youtubeAccountEntity, cq, cb) -> {
            Join<YoutubeAccountEntity, YoutubeAccountLinkEntity> accountJoinAccountLink = youtubeAccountEntity.join(YoutubeAccountEntity_.ACCOUNT_LINKS, JoinType.INNER);
            Join<YoutubeAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer = accountJoinAccountLink.join(YoutubeAccountLinkEntity_.USER, JoinType.INNER);
            return cb.like(accountLinkJoinCustomer.get(CustomerEntity_.USER_ID), BusinessConstants.PERCENT_SIGN + userId + BusinessConstants.PERCENT_SIGN);
        };
    }

    public static Specification<YoutubeAccountEntity> accountStatusIn(List<String> status) {
        return (youtubeAccountEntity, cq, cb) -> cb.in(youtubeAccountEntity.get(YoutubeAccountEntity_.ACCOUNT_STATUS)).value(status);
    }

    public static Specification<YoutubeAccountEntity> accountNameEqual(String accountName) {
        return (youtubeAccountEntity, cq, cb) -> cb.equal(youtubeAccountEntity.get(YoutubeAccountEntity_.ACCOUNT_NAME), accountName);
    }

    public static Specification<YoutubeAccountEntity> customerStatusIn(List<String> customerStatus) {
        return (youtubeAccountEntity, cq, cb) -> {
            Join<YoutubeAccountEntity, YoutubeAccountLinkEntity> accountJoinAccountLink = youtubeAccountEntity.join(YoutubeAccountEntity_.ACCOUNT_LINKS, JoinType.INNER);
            Join<YoutubeAccountLinkEntity, CustomerEntity> accountLinkJoinCustomer = accountJoinAccountLink.join(YoutubeAccountLinkEntity_.USER, JoinType.INNER);
            return cb.in(accountLinkJoinCustomer.get(CustomerEntity_.CUSTOMER_STATUS)).value(customerStatus);
        };
    }

    public static Specification<YoutubeAccountLinkEntity> overlapAddedDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (root, cq, cb) ->
                cb.and(
                        cb.lessThanOrEqualTo(root.get(YoutubeAccountLinkEntity_.ADDED_DATE), endDate),
                        cb.greaterThanOrEqualTo(root.get(YoutubeAccountLinkEntity_.ADDED_DATE), startDate)
                );
    }
}
