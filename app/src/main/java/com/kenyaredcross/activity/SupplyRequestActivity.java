package com.kenyaredcross.activity;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_request);

        supplyRequestRecyclerView = findViewById(R.id.supplyRequests);
        supplyRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Firebase database reference
        DatabaseReference supplyRequestRef = FirebaseDatabase.getInstance().getReference().child("SupplyRequest");

        // Create a query to filter by "pending" status
        Query pendingRequestsQuery = supplyRequestRef.orderByChild("status").equalTo("pending");

        // Set up FirebaseRecyclerOptions with the filtered query
        FirebaseRecyclerOptions<SupplyRequestModel> options =
                new FirebaseRecyclerOptions.Builder<SupplyRequestModel>()
                        .setQuery(pendingRequestsQuery, SupplyRequestModel.class)
                        .build();

        // Initialize adapter with options
        adapter = new SupplyRequestAdapter(options);
        supplyRequestRecyclerView.setAdapter(adapter);
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
