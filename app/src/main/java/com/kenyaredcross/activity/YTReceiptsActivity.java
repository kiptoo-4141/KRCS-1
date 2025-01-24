
// Updated YTReceiptsActivity
package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ytreceipts);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("CoursePayments").child("youth1@gmail_com");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Receipt> receiptList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Receipt receipt = child.getValue(Receipt.class);
                    receiptList.add(receipt);
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
