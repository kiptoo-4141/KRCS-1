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
import com.kenyaredcross.domain_model.GroupModel;
import com.kenyaredcross.domain_model.VolunteerModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatorBorrowedEquipmentActivity extends AppCompatActivity
        implements BorrowedEquipmentAdapter3.OnReturnButtonClickListener {

    private RecyclerView recyclerView;
    private BorrowedEquipmentAdapter3 adapter;
    private List<BorrowedEquipmentModel3> borrowedEquipmentList;
    private DatabaseReference borrowedEquipmentRef;
    private DatabaseReference returnedEquipmentRef;
    private DatabaseReference usersRef;
    private DatabaseReference groupsRef;
    private FirebaseAuth firebaseAuth;
    private String coordinatorEmail;

    private List<VolunteerModel> volunteerList;
    private List<GroupModel> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_borrowed_equipment);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowedEquipmentList = new ArrayList<>();
        adapter = new BorrowedEquipmentAdapter3(borrowedEquipmentList, this);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        coordinatorEmail = firebaseAuth.getCurrentUser().getEmail();

        borrowedEquipmentRef = FirebaseDatabase.getInstance().getReference("BorrowedEquipments");
        returnedEquipmentRef = FirebaseDatabase.getInstance().getReference("ReturnedEquipments");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference("groups");

        volunteerList = new ArrayList<>();
        groupList = new ArrayList<>();

        loadBorrowedEquipments();
        loadVolunteers();
        loadGroups();
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

    private void loadVolunteers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                volunteerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VolunteerModel volunteer = dataSnapshot.getValue(VolunteerModel.class);
                    if (volunteer != null && "Volunteer".equals(volunteer.getRole()) && "approved".equals(volunteer.getStatus())) {
                        volunteerList.add(volunteer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CoordinatorBorrowedEquipmentActivity.this, "Failed to load volunteers.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroups() {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GroupModel group = dataSnapshot.getValue(GroupModel.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CoordinatorBorrowedEquipmentActivity.this, "Failed to load groups.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReturnButtonClick(BorrowedEquipmentModel3 borrowedEquipment) {
        // Update the status in BorrowedEquipments to "returned"
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "returned");

        borrowedEquipmentRef.child(borrowedEquipment.getKey()).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Move the item to ReturnedEquipments
                        Map<String, Object> returnedEquipment = new HashMap<>();
                        returnedEquipment.put("username", borrowedEquipment.getUsername());
                        returnedEquipment.put("email", borrowedEquipment.getEmail());
                        returnedEquipment.put("itemName", borrowedEquipment.getItemName());
                        returnedEquipment.put("category", borrowedEquipment.getCategory());
                        returnedEquipment.put("count", borrowedEquipment.getCount());
                        returnedEquipment.put("date", borrowedEquipment.getDate());
                        returnedEquipment.put("status", "returned");
                        returnedEquipment.put("returnDate", System.currentTimeMillis());

                        returnedEquipmentRef.child(borrowedEquipment.getKey()).setValue(returnedEquipment)
                                .addOnCompleteListener(returnTask -> {
                                    if (returnTask.isSuccessful()) {
                                        // The adapter will automatically update because we're using a ValueEventListener
                                        // that refreshes the list when data changes
                                        Toast.makeText(this, "Equipment returned successfully: " + borrowedEquipment.getItemName(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Failed to add to returned equipment.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Failed to update equipment status.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}