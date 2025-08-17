package com.zax.maybank_assessment.repo;

import com.zax.maybank_assessment.domain.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TransactionSpecs {
    private TransactionSpecs() {}

    public static Specification<Transaction> filter(
            String customerId, List<String> accountNumbers, String description) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null && !customerId.isBlank()) {
                predicates.add(cb.equal(root.get("customerId"), customerId));
            }
            if (accountNumbers != null && !accountNumbers.isEmpty()) {
                predicates.add(root.get("accountNumber").in(accountNumbers));
            }
            if (description != null && !description.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.upper(root.get("description")),
                                "%" + description.toUpperCase() + "%"
                        )
                );
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}