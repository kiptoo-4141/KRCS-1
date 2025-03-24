package com.kenyaredcross.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.SupplyReceiptAdapter;
import com.kenyaredcross.domain_model.PaidRequest;
import com.kenyaredcross.domain_model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplyReceiptsActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 1001;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noReceiptsText;
    private SupplyReceiptAdapter adapter;
    private List<PaidRequest> paidRequests = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private Map<String, User> userMap = new HashMap<>();
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_receipts);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Supply Receipts");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noReceiptsText = findViewById(R.id.noReceiptsText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check if we have storage permissions
        checkStoragePermission();

        // Get logged in user email
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loggedInUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            fetchPaidRequests();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ we don't need runtime permission for downloading to Downloads folder
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission required to save receipts", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchPaidRequests() {
        progressBar.setVisibility(View.VISIBLE);
        noReceiptsText.setVisibility(View.GONE);

        DatabaseReference paidRequestsRef = FirebaseDatabase.getInstance().getReference("PaidRequests");
        paidRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paidRequests.clear();
                userMap.clear();
                users.clear();

                // Keep track of supplier emails to fetch
                List<String> suppliersToFetch = new ArrayList<>();

                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    try {
                        // Manual mapping to avoid type conversion issues
                        PaidRequest paidRequest = new PaidRequest();

                        // Handle each field with proper type checking
                        if (requestSnapshot.child("amount").exists()) {
                            Object amountObj = requestSnapshot.child("amount").getValue();
                            if (amountObj instanceof Long) {
                                paidRequest.setAmount(String.valueOf(amountObj));
                            } else if (amountObj instanceof String) {
                                paidRequest.setAmount((String) amountObj);
                            }
                        }

                        if (requestSnapshot.child("category").exists()) {
                            paidRequest.setCategory(String.valueOf(requestSnapshot.child("category").getValue()));
                        }

                        if (requestSnapshot.child("dateTime").exists()) {
                            paidRequest.setDateTime(String.valueOf(requestSnapshot.child("dateTime").getValue()));
                        }

                        if (requestSnapshot.child("inventoryManager").exists()) {
                            paidRequest.setInventoryManager(String.valueOf(requestSnapshot.child("inventoryManager").getValue()));
                        }

                        if (requestSnapshot.child("itemName").exists()) {
                            paidRequest.setItemName(String.valueOf(requestSnapshot.child("itemName").getValue()));
                        }

                        if (requestSnapshot.child("requestCount").exists()) {
                            Object countObj = requestSnapshot.child("requestCount").getValue();
                            if (countObj instanceof Long) {
                                paidRequest.setRequestCount(String.valueOf(countObj));
                            } else if (countObj instanceof String) {
                                paidRequest.setRequestCount((String) countObj);
                            }
                        }

                        if (requestSnapshot.child("status").exists()) {
                            paidRequest.setStatus(String.valueOf(requestSnapshot.child("status").getValue()));
                        }

                        if (requestSnapshot.child("supplier").exists()) {
                            paidRequest.setSupplier(String.valueOf(requestSnapshot.child("supplier").getValue()));
                        }

                        // Check if this is an approved request
                        if (paidRequest.getStatus() != null && paidRequest.getStatus().equals("approved")) {
                            // Check if we're viewing as supplier or admin
                            String supplierEmail = paidRequest.getSupplier();
                            if (supplierEmail != null && (loggedInUserEmail.equals(supplierEmail) ||
                                    loggedInUserEmail.equals("admin@kenyaredcross.org"))) {
                                paidRequests.add(paidRequest);

                                // Add supplier to list for fetching if not already there
                                if (!userMap.containsKey(supplierEmail) &&
                                        !suppliersToFetch.contains(supplierEmail)) {
                                    suppliersToFetch.add(supplierEmail);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("SupplyReceiptsActivity", "Error parsing paid request: " + e.getMessage());
                    }
                }

                if (paidRequests.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    noReceiptsText.setVisibility(View.VISIBLE);
                } else if (!suppliersToFetch.isEmpty()) {
                    // Fetch all suppliers in parallel
                    fetchUserDetails(suppliersToFetch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SupplyReceiptsActivity.this,
                        "Error loading receipts: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("SupplyReceiptsActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchUserDetails(List<String> supplierEmails) {
        if (supplierEmails.isEmpty()) {
            // No suppliers to fetch, update UI
            updateUI();
            return;
        }

        final int[] fetchCount = {0};
        final int totalToFetch = supplierEmails.size();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        for (String email : supplierEmails) {
            String sanitizedEmail = email.replace(".", "_");
            usersRef.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userMap.put(email, user);
                    }

                    fetchCount[0]++;
                    if (fetchCount[0] >= totalToFetch) {
                        // All users fetched, update UI
                        prepareDataAndUpdateUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    fetchCount[0]++;
                    if (fetchCount[0] >= totalToFetch) {
                        // Continue even with errors
                        prepareDataAndUpdateUI();
                    }
                }
            });
        }
    }

    private void prepareDataAndUpdateUI() {
        users.clear();
        // For each paid request, get corresponding user
        for (PaidRequest request : paidRequests) {
            User user = userMap.get(request.getSupplier());
            if (user != null) {
                users.add(user);
            } else {
                // Create placeholder user if not found
                User placeholder = new User();
                placeholder.setUsername("Unknown Supplier");
                placeholder.setEmail(request.getSupplier());
                users.add(placeholder);
            }
        }

        updateUI();
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);

        if (paidRequests.isEmpty()) {
            noReceiptsText.setVisibility(View.VISIBLE);
            return;
        }

        adapter = new SupplyReceiptAdapter(paidRequests, users, SupplyReceiptsActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}