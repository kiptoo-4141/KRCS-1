package com.kenyaredcross.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.SupplyRequestAdapter;
import com.kenyaredcross.domain_model.SupplyRequestModel;

public class SupplyRequestActivity extends AppCompatActivity {
    private RecyclerView supplyRequestRecyclerView;
    private SupplyRequestAdapter adapter;
    private TextView noRequestsText;
    private String currentSupplierEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_request);

        // Initialize views
        supplyRequestRecyclerView = findViewById(R.id.supplyRequests);
        noRequestsText = findViewById(R.id.tvNoRequests);
        supplyRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the logged-in supplier's email from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("supplierEmail")) {
            currentSupplierEmail = intent.getStringExtra("supplierEmail");
        } else {
            Toast.makeText(this, "Supplier email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up the adapter
        setupAdapter();
    }

    private void setupAdapter() {
        DatabaseReference supplyRequestRef = FirebaseDatabase.getInstance().getReference().child("SupplyRequest");
        Query supplierRequestsQuery = supplyRequestRef.orderByChild("supplier").equalTo(currentSupplierEmail);

        FirebaseRecyclerOptions<SupplyRequestModel> options =
                new FirebaseRecyclerOptions.Builder<SupplyRequestModel>()
                        .setQuery(supplierRequestsQuery, SupplyRequestModel.class)
                        .build();

        adapter = new SupplyRequestAdapter(options);
        supplyRequestRecyclerView.setAdapter(adapter);

        // Update empty view
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateEmptyView();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateEmptyView();
            }
        });
    }

    private void updateEmptyView() {
        if (adapter.getItemCount() == 0) {
            noRequestsText.setVisibility(TextView.VISIBLE);
            supplyRequestRecyclerView.setVisibility(RecyclerView.GONE);
        } else {
            noRequestsText.setVisibility(TextView.GONE);
            supplyRequestRecyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}