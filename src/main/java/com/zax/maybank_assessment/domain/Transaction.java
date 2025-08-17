package com.zax.maybank_assessment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "transactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_txn_natural",
                columnNames = {
                        "account_number", "trx_amount", "trx_date", "trx_time", "customer_id", "description"
                }
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "trx_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal trxAmount;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "trx_date", nullable = false)
    private LocalDate trxDate;

    @Column(name = "trx_time", nullable = false)
    private LocalTime trxTime;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    /** Used by JPA for optimistic locking (for concurrent update handling) */
    @Version
    private Long version;
}