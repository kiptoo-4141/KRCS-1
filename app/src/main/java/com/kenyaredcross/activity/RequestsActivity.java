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
import com.kenyaredcross.adapters.RequestAdapter;

import com.kenyaredcross.domain_model.RequestModel;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<RequestModel> requestList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        adapter = new RequestAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Enrollments");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot courseSnapshot : userSnapshot.getChildren()) {
                        RequestModel request = courseSnapshot.getValue(RequestModel.class);
                        requestList.add(request);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}