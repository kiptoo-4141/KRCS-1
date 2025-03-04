package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.FeedbacksAdapter;
import com.kenyaredcross.domain_model.FeedbacksModel;
import java.util.*;

public class FeedbacksActivity extends AppCompatActivity {

    private EditText messageInput;
    private Spinner userSpinner;
    private Button sendFeedbackButton;
    private RecyclerView feedbackRecyclerView;

    private DatabaseReference feedbackRef, usersRef;
    private List<String> userList = new ArrayList<>();
    private List<FeedbacksModel> feedbackList = new ArrayList<>();
    private FeedbacksAdapter feedbacksAdapter;

    private String senderEmail = "";
    private String selectedReceiverEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        messageInput = findViewById(R.id.messageInput);
        userSpinner = findViewById(R.id.userSpinner);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);

        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbacksAdapter = new FeedbacksAdapter(feedbackList);
        feedbackRecyclerView.setAdapter(feedbacksAdapter);

        getLoggedInUser();
    }

    private void getLoggedInUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            senderEmail = currentUser.getEmail();
            loadUsers();
            setupUserSelection();
            setupSendButton();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no user is logged in
        }
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    if (email != null && !email.equals(senderEmail)) {
                        userList.add(email);
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

    private void setupUserSelection() {
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReceiverEmail = userList.get(position);
                if (!selectedReceiverEmail.equals("No users available")) {
                    loadConversation(selectedReceiverEmail);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadConversation(String receiverEmail) {
        feedbackRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();

                for (DataSnapshot messageSnap : snapshot.getChildren()) {
                    FeedbacksModel message = messageSnap.getValue(FeedbacksModel.class);
                    if (message != null &&
                            ((message.getSenderEmail().equals(senderEmail) && message.getReceiverEmail().equals(receiverEmail)) ||
                                    (message.getSenderEmail().equals(receiverEmail) && message.getReceiverEmail().equals(senderEmail)))) {
                        feedbackList.add(message);
                    }
                }

                feedbacksAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedbacksActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSendButton() {
        sendFeedbackButton.setOnClickListener(v -> sendFeedback());
    }

    private void sendFeedback() {
        if (selectedReceiverEmail.isEmpty() || selectedReceiverEmail.equals("No users available")) {
            Toast.makeText(this, "No recipient selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        FeedbacksModel feedback = new FeedbacksModel(feedbackId, senderEmail, selectedReceiverEmail, messageText, timestamp);

        feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
                    messageInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show());
    }
}
