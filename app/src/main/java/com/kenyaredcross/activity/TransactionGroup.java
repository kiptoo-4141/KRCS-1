package com.kenyaredcross.activity;

import java.util.List;

public class TransactionGroup {
    private final String groupName; // e.g., "CoursePayments", "Donations", "PaidRequests"
    private final List<Transaction> transactions;

    public TransactionGroup(String groupName, List<Transaction> transactions) {
        this.groupName = groupName;
        this.transactions = transactions;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}