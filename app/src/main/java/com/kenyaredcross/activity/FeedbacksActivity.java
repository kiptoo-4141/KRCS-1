package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.FeedbacksModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeedbacksActivity extends AppCompatActivity {

    private EditText messageInput;
    private Spinner userSpinner;
    private Button sendFeedbackButton;
    private DatabaseReference feedbackRef, usersRef;
    private final List<String> userList = new ArrayList<>();
    private final String senderId = "currentUserEmail"; // Replace with actual sender
    private final String currentUserRole = "Youth"; // Replace with actual user role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        messageInput = findViewById(R.id.messageInput);
        userSpinner = findViewById(R.id.userSpinner);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);

        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadUsers(); // Load users dynamically

        sendFeedbackButton.setOnClickListener(v -> sendFeedback());
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String username = userSnap.child("username").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);
                    String email = userSnap.child("email").getValue(String.class);

                    if (username != null && role != null && email != null) {
                        // Apply role-based filtering
                        if (isValidRecipient(role)) {
                            String displayText = username + " (" + role + ") - " + email;
                            userList.add(displayText);
                        }
                    }
                }

                if (userList.isEmpty()) {
                    userList.add("No users available");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(FeedbacksActivity.this, android.R.layout.simple_spinner_dropdown_item, userList);
                userSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedbacksActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isValidRecipient(String role) {
        switch (currentUserRole) {
            case "Youth":
                return !(role.equals("Volunteer") || role.equals("Supplier") || role.equals("Youth"));
            case "Supplier":
                return role.equals("Inventory Manager") || role.equals("Finance Manager") || role.equals("Service Manager");
            default:
                return true; // Other roles can send to anyone
        }
    }

    private void sendFeedback() {
        if (userSpinner.getSelectedItem() == null || userSpinner.getSelectedItem().toString().equals("No users available")) {
            Toast.makeText(this, "No valid recipient selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String receiverId = userSpinner.getSelectedItem().toString();
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        FeedbacksModel feedback = new FeedbacksModel(feedbackId, senderId, receiverId, message, timestamp);
        feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Feedback sent!", Toast.LENGTH_SHORT).show();
                    messageInput.setText(""); // Clear input after sending
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send feedback", Toast.LENGTH_SHORT).show());
    }
}
