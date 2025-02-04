package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.RequestedItemsAdapter;
import com.kenyaredcross.domain_model.RequestedItem;

import java.util.ArrayList;
import java.util.List;

public class RequestedItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestedItemsAdapter adapter;
    private List<RequestedItem> itemList;
    private DatabaseReference itemRequestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requested_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        adapter = new RequestedItemsAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        itemRequestsRef = FirebaseDatabase.getInstance().getReference("ItemRequests");

        loadRequestedItems();
    }

    private void loadRequestedItems() {
        itemRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    RequestedItem item = itemSnapshot.getValue(RequestedItem.class);
                    if (item != null) {
                        item.setRequestId(itemSnapshot.getKey()); // Store the Firebase key
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestedItemsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
