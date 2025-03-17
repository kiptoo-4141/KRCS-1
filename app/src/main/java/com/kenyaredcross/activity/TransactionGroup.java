package com.kenyaredcross.activity;

import java.util.List;

public class TransactionGroup {
    private String groupName; // e.g., "CoursePayments", "Donations", "PaidRequests"
    private List<Transaction> transactions;

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