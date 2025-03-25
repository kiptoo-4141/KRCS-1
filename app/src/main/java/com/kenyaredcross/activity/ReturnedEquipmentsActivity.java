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
import com.kenyaredcross.adapters.ReturnedItemsAdapter;
import com.kenyaredcross.domain_model.ReturnedItem;

import java.util.ArrayList;
import java.util.List;

public class ReturnedEquipmentsActivity extends AppCompatActivity
        implements ReturnedItemsAdapter.OnApproveClickListener {

    private RecyclerView recyclerView;
    private ReturnedItemsAdapter adapter;
    private List<ReturnedItem> returnedItems;
    private DatabaseReference itemRequestsRef;
    private DatabaseReference borrowedEquipmentsRef;
    private DatabaseReference inventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returned_equipments);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        returnedItems = new ArrayList<>();
        adapter = new ReturnedItemsAdapter(returnedItems, this);
        recyclerView.setAdapter(adapter);

        itemRequestsRef = FirebaseDatabase.getInstance().getReference("ItemRequests");
        borrowedEquipmentsRef = FirebaseDatabase.getInstance().getReference("BorrowedEquipments");
        inventoryRef = FirebaseDatabase.getInstance().getReference("OrganisationInventory");

        loadReturnedItems();
    }

    private void loadReturnedItems() {
        // Load from ItemRequests
        itemRequestsRef.orderByChild("status").equalTo("returned")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ReturnedItem item = new ReturnedItem();
                            item.setKey(dataSnapshot.getKey());
                            item.setSource("ItemRequests");
                            item.setItemName(dataSnapshot.child("itemName").getValue(String.class));
                            item.setQuantity(dataSnapshot.child("quantity").getValue(Integer.class));
                            item.setStatus(dataSnapshot.child("status").getValue(String.class));
                            item.setUserEmail(dataSnapshot.child("trainerEmail").getValue(String.class));
                            returnedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReturnedEquipmentsActivity.this,
                                "Failed to load item requests", Toast.LENGTH_SHORT).show();
                    }
                });

        // Load from BorrowedEquipments
        borrowedEquipmentsRef.orderByChild("status").equalTo("returned")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ReturnedItem item = new ReturnedItem();
                            item.setKey(dataSnapshot.getKey());
                            item.setSource("BorrowedEquipments");
                            item.setItemName(dataSnapshot.child("itemName").getValue(String.class));
                            item.setQuantity(dataSnapshot.child("count").getValue(Integer.class));
                            item.setStatus(dataSnapshot.child("status").getValue(String.class));
                            item.setUserEmail(dataSnapshot.child("email").getValue(String.class));
                            item.setCategory(dataSnapshot.child("category").getValue(String.class));
                            returnedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReturnedEquipmentsActivity.this,
                                "Failed to load borrowed equipment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onApproveClick(ReturnedItem returnedItem) {
        // Update status to "return approved"
        DatabaseReference sourceRef;
        if ("ItemRequests".equals(returnedItem.getSource())) {
            sourceRef = itemRequestsRef.child(returnedItem.getKey());
        } else {
            sourceRef = borrowedEquipmentsRef.child(returnedItem.getKey());
        }

        sourceRef.child("status").setValue("return approved")
                .addOnSuccessListener(aVoid -> {
                    // Update inventory
                    updateInventory(returnedItem);
                    Toast.makeText(this, "Return approved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to approve return", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateInventory(ReturnedItem returnedItem) {
        inventoryRef.child(returnedItem.getItemName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Item exists in inventory - update count
                            int currentCount = snapshot.child("count").getValue(Integer.class);
                            inventoryRef.child(returnedItem.getItemName()).child("count")
                                    .setValue(currentCount + returnedItem.getQuantity());
                        } else {
                            // Item doesn't exist - create new entry
                            inventoryRef.child(returnedItem.getItemName()).child("count")
                                    .setValue(returnedItem.getQuantity());
                            if (returnedItem.getCategory() != null) {
                                inventoryRef.child(returnedItem.getItemName()).child("category")
                                        .setValue(returnedItem.getCategory());
                            }
                            inventoryRef.child(returnedItem.getItemName()).child("itemName")
                                    .setValue(returnedItem.getItemName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReturnedEquipmentsActivity.this,
                                "Failed to update inventory", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}