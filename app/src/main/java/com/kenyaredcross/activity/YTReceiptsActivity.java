package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.ReceiptAdapter;
import com.kenyaredcross.domain_model.Receipt;

import java.util.ArrayList;
import java.util.List;

public class YTReceiptsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private List<Receipt> receiptList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytreceipts);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        receiptList = new ArrayList<>();

        // Get the current logged-in user's email
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail().replace(".", "_") : "";

        if (!userEmail.isEmpty()) {
            // Reference to CoursePayments in Firebase
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CoursePayments").child(userEmail);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    receiptList.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Receipt receipt = child.getValue(Receipt.class);
                        if (receipt != null && "Approved".equals(receipt.getStatus())) {
                            receiptList.add(receipt);
                        }
                    }

                    ReceiptAdapter adapter = new ReceiptAdapter(YTReceiptsActivity.this, receiptList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(YTReceiptsActivity.this, "Failed to load receipts", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
