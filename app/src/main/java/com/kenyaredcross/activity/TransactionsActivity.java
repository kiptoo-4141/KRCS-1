package com.kenyaredcross.activity;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions);

        // Dummy data (replace with actual data retrieval logic)
        List<TransactionGroup> transactionGroups = new ArrayList<>();

        List<Transaction> coursePayments = new ArrayList<>();
        coursePayments.add(new Transaction("101", "1200", "2025-02-28", "10:20:34", "M-Pesa", "Approved", "kaje@gmail.com", "0795310625", "101"));
        transactionGroups.add(new TransactionGroup("CoursePayments", coursePayments));

        List<Transaction> donations = new ArrayList<>();
        donations.add(new Transaction("ec7d6ddd-132f-4df1-865b-15df17501e8f", "45000", "Bank Transfer", "kaje@gmail.com", "David Kaje", "1234123412341234"));
        transactionGroups.add(new TransactionGroup("Donations", donations));

        List<Transaction> paidRequests = new ArrayList<>();
        paidRequests.add(new Transaction("-OKp6ypONGIP96sWKRJx", "3000", "Training", "2025-03-08 12:39:39", "inventory@gmail.com", "First Aid Kit", "2", "approved", "chase@gmail.com"));
        transactionGroups.add(new TransactionGroup("PaidRequests", paidRequests));

        // Set up RecyclerView
        RecyclerView rvTransactionGroups = findViewById(R.id.rvTransactions);
        rvTransactionGroups.setLayoutManager(new LinearLayoutManager(this));
        TransactionAdapter adapter = new TransactionAdapter(transactionGroups);
        rvTransactionGroups.setAdapter(adapter);
    }
}