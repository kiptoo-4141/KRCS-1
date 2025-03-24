package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.ItemRequestAdapter;
import com.kenyaredcross.domain_model.ItemRequestModel;

import java.util.ArrayList;
import java.util.List;

public class InUseActivity extends AppCompatActivity implements ItemRequestAdapter.OnReturnButtonClickListener {

    private RecyclerView recyclerView;
    private ItemRequestAdapter adapter;
    private List<ItemRequestModel> itemRequestList;
    private DatabaseReference itemRequestsRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_in_use);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemRequestList = new ArrayList<>();
        adapter = new ItemRequestAdapter(itemRequestList, this);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        itemRequestsRef = FirebaseDatabase.getInstance().getReference("ItemRequests");

        loadItemRequests();
    }

    private void loadItemRequests() {
        itemRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemRequestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItemRequestModel itemRequest = dataSnapshot.getValue(ItemRequestModel.class);
                    if (itemRequest != null && currentUserEmail.equals(itemRequest.getTrainerEmail())) {
                        itemRequest.setKey(dataSnapshot.getKey());
                        itemRequestList.add(itemRequest);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InUseActivity.this, "Failed to load item requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReturnButtonClick(ItemRequestModel itemRequest) {
        // Update the status to "returned"
        itemRequestsRef.child(itemRequest.getKey()).child("status").setValue("returned")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, itemRequest.getItemName() + " marked as returned", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to mark as returned", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}