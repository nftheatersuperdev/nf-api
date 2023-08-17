package com.nftheater.api.specification;

import com.nftheater.api.entity.CustomerEntity;
import com.nftheater.api.entity.CustomerEntity_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CustomerSpecification {

    private static final String PERCENT_SIGN = "%";

    private CustomerSpecification() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static Specification<CustomerEntity> userIdContain(String userId) {
        return (root, cq, cb) -> cb.like(root.get(CustomerEntity_.USER_ID), PERCENT_SIGN + userId + PERCENT_SIGN);
    }

    public static Specification<CustomerEntity> customerNameContain(String customerName) {
        return (root, cq, cb) -> cb.like(root.get(CustomerEntity_.CUSTOMER_STATUS), PERCENT_SIGN + customerName + PERCENT_SIGN);
    }

    public static Specification<CustomerEntity> emailContain(String email) {
        return (root, cq, cb) -> cb.like(root.get(CustomerEntity_.EMAIL), PERCENT_SIGN + email + PERCENT_SIGN);
    }

    public static Specification<CustomerEntity> phoneNumberContain(String phone) {
        return (root, cq, cb) -> cb.like(root.get(CustomerEntity_.PHONE_NUMBER), PERCENT_SIGN + phone + PERCENT_SIGN);
    }

    public static Specification<CustomerEntity> lineIdContain(String lineId) {
        return (root, cq, cb) -> cb.like(root.get(CustomerEntity_.LINE_ID), PERCENT_SIGN + lineId + PERCENT_SIGN);
    }

    public static Specification<CustomerEntity> customerStatusIn(List<String> customerStatus) {
        return (root, cq, cb) -> cb.in(root.get(CustomerEntity_.CUSTOMER_STATUS)).value(customerStatus);
    }

    public static Specification<CustomerEntity> accountEqual(String account) {
        return (root, cq, cb) -> cb.equal(root.get(CustomerEntity_.ACCOUNT), account);
    }

}
