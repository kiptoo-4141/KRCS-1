package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.kenyaredcross.adapters.VolunteerCheckAdapter;
import com.kenyaredcross.domain_model.VolunteerCheckModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VolunteersCheckActivity extends AppCompatActivity {

    private RecyclerView volunteerView;
    private VolunteerCheckAdapter adapter;
    private Button group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteers_check);

        group = findViewById(R.id.grpbtn);

        // Initialize RecyclerView and set layout manager
        volunteerView = findViewById(R.id.volunteerView);
        volunteerView.setLayoutManager(new LinearLayoutManager(this));

        // Firebase query to retrieve volunteers with the "Volunteer" role
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query volunteerQuery = databaseReference.orderByChild("role").equalTo("Volunteer");

        // Configure FirebaseRecyclerAdapter options
        FirebaseRecyclerOptions<VolunteerCheckModel> options = new FirebaseRecyclerOptions.Builder<VolunteerCheckModel>()
                .setQuery(volunteerQuery, VolunteerCheckModel.class)
                .build();

        // Set up adapter
        adapter = new VolunteerCheckAdapter(options, this);
        volunteerView.setAdapter(adapter);

        // Group button click event
        group.setOnClickListener(v -> {
            List<VolunteerCheckModel> selectedVolunteers = adapter.getSelectedVolunteers();
            if (selectedVolunteers.isEmpty()) {
                Toast.makeText(this, "No volunteers selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if all selected volunteers are approved
            for (VolunteerCheckModel volunteer : selectedVolunteers) {
                if (!"approved".equalsIgnoreCase(volunteer.getStatus())) {
                    Toast.makeText(this, "All selected volunteers must be approved", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Show dialog to input group name
            showGroupNameDialog(selectedVolunteers);
        });
    }

    // Method to show the dialog for entering the group name
    private void showGroupNameDialog(List<VolunteerCheckModel> selectedVolunteers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Group Name");

        final EditText groupNameInput = new EditText(this);
        builder.setView(groupNameInput);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            String groupName = groupNameInput.getText().toString().trim();
            if (!TextUtils.isEmpty(groupName)) {
                saveGroupToFirebase(groupName, selectedVolunteers);
            } else {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Method to save group data to Firebase
    private void saveGroupToFirebase(String groupName, List<VolunteerCheckModel> selectedVolunteers) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");

        // Generate a random 7-character group ID
        String groupId = generateRandomGroupId();

        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupName", groupName);
        groupData.put("groupId", groupId);

        // Add each selected volunteer's basic details to the group data
        for (VolunteerCheckModel volunteer : selectedVolunteers) {
            String sanitizedEmail = sanitizeEmailForFirebaseKey(volunteer.getEmail()); // Sanitize email here
            Map<String, String> volunteerData = new HashMap<>();
            volunteerData.put("username", volunteer.getUsername());
            volunteerData.put("email", volunteer.getEmail());

            // Store volunteer data directly under their unique sanitized key
            groupData.put(sanitizedEmail, volunteerData);
        }

        // Save group data in Firebase
        groupsRef.child(groupId).setValue(groupData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to sanitize email for Firebase key compatibility by replacing "." with "_"
    private String sanitizeEmailForFirebaseKey(String email) {
        return email.replace(".", "_"); // Only replace "." with "_"
    }

    // Method to generate a random 7-character string for group ID
    private String generateRandomGroupId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
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
