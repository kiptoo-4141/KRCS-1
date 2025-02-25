package com.kenyaredcross.activity;

import android.os.Bundle;
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
import com.kenyaredcross.adapters.SuppliersInventoryAdapter;
import com.kenyaredcross.domain_model.SupplierInventory;

import java.util.ArrayList;
import java.util.List;

public class MyInventoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SuppliersInventoryAdapter adapter;
    private List<SupplierInventory> inventoryList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_inventory);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inventoryList = new ArrayList<>();
        adapter = new SuppliersInventoryAdapter(this, inventoryList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("SupplierInventory");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    SupplierInventory item = itemSnapshot.getValue(SupplierInventory.class);
                    inventoryList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
