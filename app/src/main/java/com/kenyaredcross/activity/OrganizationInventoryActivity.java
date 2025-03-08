package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.kenyaredcross.adapters.OrgInventoryAdapter;
import com.kenyaredcross.adapters.SuppliedGoodsAdapter;
import com.kenyaredcross.domain_model.OrgInventoryModel;
import com.kenyaredcross.domain_model.SupplierModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class OrganizationInventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView, suppliedGoodsRecyclerView;
    OrgInventoryAdapter orgInventoryAdapter;
    SuppliedGoodsAdapter suppliedGoodsAdapter;
    DatabaseReference inventoryRef, supplierInventoryRef, supplyRequestRef, usersRef;
    Spinner itemCategorySpinner, itemNameSpinner, supplierSelectionSpinner;
    Button btnRequest, btnDelete;
    EditText requestCount;
    TextView itemAmount, requesteditem;
    List<SupplierModel> suppliersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_inventory);

        // Initialize Firebase references
        inventoryRef = FirebaseDatabase.getInstance().getReference("OrganisationInventory");
        supplierInventoryRef = FirebaseDatabase.getInstance().getReference("SupplierInventory");
        supplyRequestRef = FirebaseDatabase.getInstance().getReference("SupplyRequest");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        requesteditem = findViewById(R.id.requested_item);
        requesteditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizationInventoryActivity.this, RequestedItemsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize UI components
        recyclerView = findViewById(R.id.organizationInventoryView);
        suppliedGoodsRecyclerView = findViewById(R.id.recyclerViewSuppliedGoods);
        itemCategorySpinner = findViewById(R.id.itemCategorySpinner);
        itemNameSpinner = findViewById(R.id.itemName);
        supplierSelectionSpinner = findViewById(R.id.supplierSelection);
        requestCount = findViewById(R.id.requestCount);
        itemAmount = findViewById(R.id.itemAmount);
        btnRequest = findViewById(R.id.btnRequest);
        btnDelete = findViewById(R.id.btnDelete);

        suppliersList = new ArrayList<>();

        // Set up RecyclerView for organization inventory
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orgInventoryAdapter = new OrgInventoryAdapter();
        recyclerView.setAdapter(orgInventoryAdapter);

        // Set up RecyclerView for supplied goods
        suppliedGoodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suppliedGoodsAdapter = new SuppliedGoodsAdapter();
        suppliedGoodsRecyclerView.setAdapter(suppliedGoodsAdapter);

        // Load data
        loadInventoryData();
        loadSupplierInventoryData();
        loadSuppliers();

        // Set up button listeners
        setupButtonListeners();
    }

    private void loadInventoryData() {
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OrgInventoryModel> inventoryList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OrgInventoryModel model = snapshot.getValue(OrgInventoryModel.class);
                    if (model != null) {
                        inventoryList.add(model);
                    }
                }
                orgInventoryAdapter.setInventoryList(inventoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadInventoryData:onCancelled", databaseError.toException());
            }
        });
    }

    private void loadSupplierInventoryData() {
        supplierInventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> categories = new HashSet<>();
                List<String> itemNames = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.child("category").getValue(String.class);
                    String itemName = snapshot.child("itemName").getValue(String.class);

                    if (category != null) categories.add(category);
                    if (itemName != null) itemNames.add(itemName);
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(OrganizationInventoryActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(categories));
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemCategorySpinner.setAdapter(categoryAdapter);

                ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(OrganizationInventoryActivity.this, android.R.layout.simple_spinner_item, itemNames);
                itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemNameSpinner.setAdapter(itemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadSupplierInventoryData:onCancelled", databaseError.toException());
            }
        });
    }

    private void loadSuppliers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suppliersList.clear();
                List<String> supplierDisplayNames = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String role = snapshot.child("role").getValue(String.class);

                    if ("Supplier".equals(role)) {
                        String email = snapshot.child("email").getValue(String.class);
                        String username = snapshot.child("username").getValue(String.class);

                        if (email != null && username != null) {
                            // Create a supplier model to store data
                            SupplierModel supplier = new SupplierModel();
                            supplier.setEmail(email);
                            supplier.setUsername(username);
                            suppliersList.add(supplier);

                            // Display format: Username (Email)
                            supplierDisplayNames.add(username + " (" + email + ")");
                        }
                    }
                }

                // Create and set adapter for supplier spinner
                ArrayAdapter<String> supplierAdapter = new ArrayAdapter<>(
                        OrganizationInventoryActivity.this,
                        android.R.layout.simple_spinner_item,
                        supplierDisplayNames
                );
                supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                supplierSelectionSpinner.setAdapter(supplierAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadSuppliers:onCancelled", databaseError.toException());
                Toast.makeText(OrganizationInventoryActivity.this, "Failed to load suppliers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtonListeners() {
        btnRequest.setOnClickListener(v -> {
            String selectedCategory = itemCategorySpinner.getSelectedItem().toString();
            String selectedItemName = itemNameSpinner.getSelectedItem().toString();
            String requestQuantityStr = requestCount.getText().toString();

            // Get selected supplier
            int supplierPosition = supplierSelectionSpinner.getSelectedItemPosition();
            String supplierEmail = "";

            if (supplierPosition >= 0 && supplierPosition < suppliersList.size()) {
                supplierEmail = suppliersList.get(supplierPosition).getEmail();
            }

            if (requestQuantityStr.isEmpty()) {
                Toast.makeText(this, "Please enter a request quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            if (supplierEmail.isEmpty()) {
                Toast.makeText(this, "Please select a supplier", Toast.LENGTH_SHORT).show();
                return;
            }

            int requestQuantity = Integer.parseInt(requestQuantityStr);

            String finalSupplierEmail = supplierEmail;
            supplierInventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemName = snapshot.child("itemName").getValue(String.class);
                        Integer unitPrice = snapshot.child("unitPrice").getValue(Integer.class);

                        if (itemName != null && itemName.equals(selectedItemName) && unitPrice != null) {
                            int totalPrice = requestQuantity * unitPrice;
                            itemAmount.setText(String.valueOf(totalPrice));

                            String requestId = generateUniqueRequestId();
                            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                            DatabaseReference newRequestRef = supplyRequestRef.child(requestId);
                            newRequestRef.child("category").setValue(selectedCategory);
                            newRequestRef.child("inventoryManager").setValue("inventory@gmail.com");
                            newRequestRef.child("supplier").setValue(finalSupplierEmail);
                            newRequestRef.child("itemName").setValue(selectedItemName);
                            newRequestRef.child("requestCount").setValue(requestQuantity);
                            newRequestRef.child("requestId").setValue(requestId);
                            newRequestRef.child("status").setValue("pending");
                            newRequestRef.child("timestamp").setValue(timestamp);
                            newRequestRef.child("totalAmount").setValue(totalPrice);

                            Toast.makeText(OrganizationInventoryActivity.this,
                                    "Request sent to " + finalSupplierEmail + " for " + requestQuantity +
                                            " of " + selectedItemName + " with total price: " + totalPrice, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("Firebase", "fetchItemData:onCancelled", databaseError.toException());
                }
            });
        });

        btnDelete.setOnClickListener(v -> {
            requestCount.setText("");
            itemAmount.setText("000");
            Toast.makeText(this, "Request deleted", Toast.LENGTH_SHORT).show();
        });
    }

    private String generateUniqueRequestId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder requestId = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            requestId.append(chars.charAt(random.nextInt(chars.length())));
        }
        return requestId.toString();
    }
}