package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
import com.kenyaredcross.adapters.SupplyReportsAdapter;
import com.kenyaredcross.domain_model.SupplyReportsModel;

import java.util.ArrayList;
import java.util.List;

public class SupplyReportsActivity extends AppCompatActivity {
    private RecyclerView supplyReportsView;
    private SupplyReportsAdapter supplyReportsAdapter;
    private List<SupplyReportsModel> supplyReportsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_reports);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        supplyReportsView = findViewById(R.id.SupplyReportsView);
        supplyReportsView.setLayoutManager(new LinearLayoutManager(this));
        supplyReportsList = new ArrayList<>();
        supplyReportsAdapter = new SupplyReportsAdapter(supplyReportsList);
        supplyReportsView.setAdapter(supplyReportsAdapter);

        // Get the logged-in supplier's email
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");

        // Fetch data from SuppliedGoods node
        fetchSuppliedGoodsData(currentUserEmail);

        // Set up download button
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> exportToCSV(supplyReportsList));
    }

    private void fetchSuppliedGoodsData(String supplierEmail) {
        DatabaseReference suppliedGoodsRef = mDatabase.child("SuppliedGoods");
        suppliedGoodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                supplyReportsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the entry has a "supplier" field
                    if (snapshot.hasChild("supplier")) {
                        String supplier = snapshot.child("supplier").getValue(String.class);
                        if (supplier != null && supplier.equals(supplierEmail.replace(".", "_"))) {
                            SupplyReportsModel report = snapshot.getValue(SupplyReportsModel.class);
                            if (report != null) {
                                // Fetch the username from the Users node
                                fetchUsername(report, supplierEmail);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SupplyReportsActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUsername(SupplyReportsModel report, String supplierEmail) {
        mDatabase.child("Users").child(supplierEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    report.setInventoryManager(username); // Set the username
                    supplyReportsList.add(report);
                    supplyReportsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SupplyReportsActivity.this, "Failed to fetch username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToCSV(List<SupplyReportsModel> reports) {
        // Implement CSV export logic here
        StringBuilder csvData = new StringBuilder();
        csvData.append("Item Name,Category,Request Count,Amount,Date & Time,Inventory Manager,Status\n");

        for (SupplyReportsModel report : reports) {
            csvData.append(report.getItemName()).append(",")
                    .append(report.getCategory()).append(",")
                    .append(report.getRequestCount()).append(",")
                    .append(report.getAmount()).append(",")
                    .append(report.getDateTime()).append(",")
                    .append(report.getInventoryManager()).append(",")
                    .append(report.getStatus()).append("\n");
        }

        // Save the CSV file (you can use FileWriter or a library like Apache Commons CSV)
        Toast.makeText(this, "CSV data:\n" + csvData.toString(), Toast.LENGTH_LONG).show();
    }
}