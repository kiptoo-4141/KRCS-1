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
import com.kenyaredcross.adapters.MyClassesAdapter;
import com.kenyaredcross.domain_model.MyClassesModel;

import java.util.ArrayList;
import java.util.List;

public class MyClassesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyClassesAdapter adapter;
    private List<MyClassesModel> requestList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        adapter = new MyClassesAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Enrollments");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear(); // Ensure no duplicate data
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot courseSnapshot : userSnapshot.getChildren()) {
                        MyClassesModel request = courseSnapshot.getValue(MyClassesModel.class);
                        if (request != null) {
                            requestList.add(request);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyClassesActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
