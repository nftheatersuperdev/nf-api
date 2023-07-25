package com.nftheater.api.specification;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unchecked")
public class JoinEntitySpecification {

    public static <K, Z> Join<K, Z> joinList(From<?, K> from, SingularAttribute<K, Z> attribute, JoinType joinType) {
        for (Join<K, ?> join : from.getJoins()) {
            boolean sameName = join.getAttribute().getName().equals(attribute.getName());
            if (sameName && join.getJoinType().equals(joinType)) {
                return (Join<K, Z>) join;
            }
        }
        return from.join(attribute, joinType);
    }

    public static <K, Z> ListJoin<K, Z> joinList(From<?, K> from, ListAttribute<K, Z> attribute, JoinType joinType) {
        for (Join<K, ?> join : from.getJoins()) {
            boolean sameName = join.getAttribute().getName().equals(attribute.getName());
            if (sameName && join.getJoinType().equals(joinType)) {
                return (ListJoin<K, Z>) join;
            }
        }
        return from.join(attribute, joinType);
    }

}
