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
    private SearchView userSearchView;
    private ArrayAdapter<String> userAdapter;
    private final List<String> displayList = new ArrayList<>();
    private Button sendFeedbackButton;
    private RecyclerView feedbackRecyclerView;
    private DatabaseReference feedbackRef, usersRef;
    private final List<String> userList = new ArrayList<>();
    private final List<FeedbacksModel> feedbackList = new ArrayList<>();
    private FeedbacksAdapter feedbacksAdapter;
    private String senderEmail = "";
    private String selectedReceiverEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        initializeViews();
        setupFirebase();
        getLoggedInUser();
    }

    private void initializeViews() {
        userSearchView = findViewById(R.id.userSearchView);
        messageInput = findViewById(R.id.messageInput);
        userSpinner = findViewById(R.id.userSpinner);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);

        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbacksAdapter = new FeedbacksAdapter(feedbackList);
        feedbackRecyclerView.setAdapter(feedbacksAdapter);

        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });
    }

    private void setupFirebase() {
        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
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
                displayList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    String username = userSnap.child("username").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    if (email != null && !email.equals(senderEmail)) {
                        username = (username != null) ? username : "Unknown";
                        role = (role != null) ? role : "No role";

                        String displayText = username + " (" + role + ") - " + email;
                        displayList.add(displayText);
                        userList.add(email);
                    }
                }

                if (displayList.isEmpty()) {
                    displayList.add("No users available");
                }

                userAdapter = new ArrayAdapter<>(FeedbacksActivity.this, android.R.layout.simple_spinner_dropdown_item, displayList);
                userSpinner.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedbacksActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String user : displayList) {
            if (user.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userAdapter.clear();
        userAdapter.addAll(filteredList);
        userAdapter.notifyDataSetChanged();
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

    private void loadConversation(String selectedReceiverEmail) {
        feedbackList.clear();

        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();

                for (DataSnapshot conversationSnap : snapshot.getChildren()) {
                    for (DataSnapshot messageSnap : conversationSnap.getChildren()) {
                        FeedbacksModel message = messageSnap.getValue(FeedbacksModel.class);
                        if (message != null && message.getSenderEmail() != null && message.getReceiverEmail() != null) {
                            if (message.getSenderEmail().equals(senderEmail) || message.getReceiverEmail().equals(senderEmail)) {
                                feedbackList.add(message);
                            }
                        }
                    }
                }

                feedbacksAdapter.notifyDataSetChanged();
                feedbackRecyclerView.scrollToPosition(feedbackList.size() - 1);
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

        String feedbackId = generateFeedbackId(senderEmail, selectedReceiverEmail);
        long timestamp = System.currentTimeMillis();

        FeedbacksModel feedback = new FeedbacksModel(UUID.randomUUID().toString(), senderEmail, selectedReceiverEmail, messageText, timestamp);

        feedbackRef.child(feedbackId).push().setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
                    messageInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show());
    }

    private String generateFeedbackId(String sender, String receiver) {
        List<String> sortedEmails = Arrays.asList(sender, receiver);
        Collections.sort(sortedEmails);
        return sortedEmails.get(0).replace(".", "_") + "_" + sortedEmails.get(1).replace(".", "_");
    }
}