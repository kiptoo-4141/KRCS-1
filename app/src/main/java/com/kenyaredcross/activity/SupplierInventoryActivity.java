package com.kenyaredcross.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;

import com.kenyaredcross.adapters.SupplierInventoryAdapter;
import com.kenyaredcross.domain_model.SupplierInventoryModel;

public class SupplierInventoryActivity extends AppCompatActivity {

    RecyclerView inventoryRecyclerView;
    SupplierInventoryAdapter adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_inventory);

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        inventoryRecyclerView = findViewById(R.id.inventoryView);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Reference to the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SupplierInventory");

        // Set up FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<SupplierInventoryModel> options =
                new FirebaseRecyclerOptions.Builder<SupplierInventoryModel>()
                        .setQuery(databaseReference, SupplierInventoryModel.class)
                        .build();

        adapter = new SupplierInventoryAdapter(options);
        inventoryRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
