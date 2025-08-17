package com.zax.maybank_assessment;

import com.zax.maybank_assessment.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

// Zax: This is just for testing the lombok Getter Setter working fine.
public class DemoRunner {
    public static void main(String[] args) {
        Transaction txn = new Transaction();
        txn.setAccountNumber("12345");
        txn.setTrxAmount(new BigDecimal("500.00"));
        txn.setDescription("Deposit");
        txn.setTrxDate(LocalDate.of(2025, 8, 16));
        txn.setTrxTime(LocalTime.of(14, 30));
        txn.setCustomerId("CUST01");

        System.out.println("Account: " + txn.getAccountNumber());
        System.out.println("Amount: " + txn.getTrxAmount());
        System.out.println("Description: " + txn.getDescription());
        System.out.println("Date: " + txn.getTrxDate());
        System.out.println("Time: " + txn.getTrxTime());
        System.out.println("Customer: " + txn.getCustomerId());
    }
}