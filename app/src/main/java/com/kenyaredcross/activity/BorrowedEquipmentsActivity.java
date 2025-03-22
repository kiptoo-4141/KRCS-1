package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

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
import com.kenyaredcross.domain_model.BorrowedEquipmentModel2;

import java.util.ArrayList;
import java.util.List;

public class BorrowedEquipmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BorrowedEquipmentAdapter adapter;
    private List<BorrowedEquipmentModel2> borrowedEquipmentList;
    private DatabaseReference borrowedEquipmentRef;
    private DatabaseReference organisationInventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed_equipments);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowedEquipmentList = new ArrayList<>();
        adapter = new BorrowedEquipmentAdapter(borrowedEquipmentList, this::onApproveButtonClick);
        recyclerView.setAdapter(adapter);

        borrowedEquipmentRef = FirebaseDatabase.getInstance().getReference("BorrowedEquipments");
        organisationInventoryRef = FirebaseDatabase.getInstance().getReference("OrganisationInventory");

        loadBorrowedEquipments();
    }

    private void loadBorrowedEquipments() {
        borrowedEquipmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                borrowedEquipmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BorrowedEquipmentModel2 borrowedEquipment = dataSnapshot.getValue(BorrowedEquipmentModel2.class);
                    if (borrowedEquipment != null && "pending".equals(borrowedEquipment.getStatus())) {
                        borrowedEquipment.setKey(dataSnapshot.getKey()); // Store the key for later updates
                        borrowedEquipmentList.add(borrowedEquipment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BorrowedEquipmentsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onApproveButtonClick(BorrowedEquipmentModel2 borrowedEquipment) {
        if ("approved".equals(borrowedEquipment.getStatus())) {
            Toast.makeText(this, "This item is already approved.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the status of the existing node
        borrowedEquipmentRef.child(borrowedEquipment.getKey()).child("status").setValue("approved")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the OrganisationInventory count
                        updateInventoryCount(borrowedEquipment);
                        Toast.makeText(this, "Item approved successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to approve item.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateInventoryCount(BorrowedEquipmentModel2 borrowedEquipment) {
        organisationInventoryRef.child(borrowedEquipment.getItemName()).child("count")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCount = snapshot.getValue(Integer.class);
                        if (currentCount != null) {
                            int newCount = currentCount - borrowedEquipment.getCount();
                            organisationInventoryRef.child(borrowedEquipment.getItemName()).child("count").setValue(newCount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BorrowedEquipmentsActivity.this, "Failed to update inventory.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}