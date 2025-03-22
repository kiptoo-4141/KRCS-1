package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.BorrowedEquipmentAdapter3;
import com.kenyaredcross.domain_model.BorrowedEquipmentModel3;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorBorrowedEquipmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BorrowedEquipmentAdapter3 adapter;
    private List<BorrowedEquipmentModel3> borrowedEquipmentList;
    private DatabaseReference borrowedEquipmentRef;
    private DatabaseReference usersRef;
    private DatabaseReference groupsRef;
    private FirebaseAuth firebaseAuth;
    private String coordinatorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_borrowed_equipment);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowedEquipmentList = new ArrayList<>();
        adapter = new BorrowedEquipmentAdapter3(borrowedEquipmentList, this::onAssignButtonClick);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        coordinatorEmail = firebaseAuth.getCurrentUser().getEmail();

        borrowedEquipmentRef = FirebaseDatabase.getInstance().getReference("BorrowedEquipments");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference("groups");

        loadBorrowedEquipments();
    }

    private void loadBorrowedEquipments() {
        borrowedEquipmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                borrowedEquipmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BorrowedEquipmentModel3 borrowedEquipment = dataSnapshot.getValue(BorrowedEquipmentModel3.class);
                    if (borrowedEquipment != null && coordinatorEmail.equals(borrowedEquipment.getEmail())) {
                        borrowedEquipment.setKey(dataSnapshot.getKey()); // Store the key for later updates
                        borrowedEquipmentList.add(borrowedEquipment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CoordinatorBorrowedEquipmentActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAssignButtonClick(BorrowedEquipmentModel3 borrowedEquipment) {
        // Open a dialog or activity to assign equipment to a volunteer or group
        // For now, we'll just show a toast
        Toast.makeText(this, "Assign equipment: " + borrowedEquipment.getItemName(), Toast.LENGTH_SHORT).show();
    }
}