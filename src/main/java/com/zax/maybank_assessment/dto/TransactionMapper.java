package com.zax.maybank_assessment.dto;

import com.zax.maybank_assessment.domain.Transaction;

/** Simple mapper: Entity -> DTO (DTO -> Entity not needed for this assessment). */
public final class TransactionMapper {
    private TransactionMapper() {}

    public static TransactionDto toDto(Transaction t) {
        if (t == null) return null;
        return TransactionDto.builder()
                .id(t.getId())
                .accountNumber(t.getAccountNumber())
                .trxAmount(t.getTrxAmount())
                .description(t.getDescription())
                .trxDate(t.getTrxDate())
                .trxTime(t.getTrxTime())
                .customerId(t.getCustomerId())
                .version(t.getVersion())
                .build();
    }
}