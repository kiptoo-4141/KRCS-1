package com.kenyaredcross.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.kenyaredcross.domain_model.User;

import java.util.ArrayList;
import java.util.List;

public class ViewFeedbacksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private DatabaseReference feedbackRef, usersRef;
    private EditText replyInput;
    private Button sendReplyButton;
    private Spinner userSpinner;

    private final String currentUserEmail = "finance@gmail_com";
    private String selectedRecipientEmail = "";

    private List<String> userList;
    private List<String> userEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedbacks);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        replyInput = findViewById(R.id.replyInput);
        sendReplyButton = findViewById(R.id.sendReplyButton);
        userSpinner = findViewById(R.id.userSpinner);

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList, currentUserEmail);
        recyclerView.setAdapter(feedbackAdapter);

        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadUsers();

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedRecipientEmail = userEmails.get(position);
                loadFeedbacks();  // Reload feedbacks based on the selected email
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected, if necessary
            }
        });

        sendReplyButton.setOnClickListener(view -> sendReply());
    }

    private void loadUsers() {
        userList = new ArrayList<>();
        userEmails = new ArrayList<>();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                userEmails.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user != null && !user.getEmail().equals(currentUserEmail)) {
                        userList.add(user.getUsername());
                        userEmails.add(user.getEmail());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewFeedbacksActivity.this, android.R.layout.simple_spinner_dropdown_item, userList);
                userSpinner.setAdapter(adapter);

                // Load feedbacks for the first user after loading the spinner
                if (!userEmails.isEmpty()) {
                    selectedRecipientEmail = userEmails.get(0);
                    loadFeedbacks();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFeedbacksActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFeedbacks() {
        if (selectedRecipientEmail.isEmpty()) return;

        String conversationId = generateConversationId(currentUserEmail, selectedRecipientEmail);
        DatabaseReference chatRef = feedbackRef.child(conversationId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Feedback feedback = data.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFeedbacksActivity.this, "Failed to load feedbacks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendReply() {
        String replyText = replyInput.getText().toString().trim();
        if (!TextUtils.isEmpty(replyText) && !selectedRecipientEmail.isEmpty()) {
            String feedbackId = feedbackRef.push().getKey();
            Feedback reply = new Feedback(
                    feedbackId,
                    "Reply Message",
                    "2025-02-06",
                    currentUserEmail,
                    replyText,
                    selectedRecipientEmail,
                    "Admin",
                    "sent",
                    "12:00:00"
            );

            String conversationId = generateConversationId(currentUserEmail, selectedRecipientEmail);
            DatabaseReference chatRef = feedbackRef.child(conversationId);
            if (feedbackId != null) {
                chatRef.child(feedbackId).setValue(reply);
                replyInput.setText("");
                Toast.makeText(this, "Reply sent", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateConversationId(String email1, String email2) {
        String sanitizedEmail1 = sanitizeEmail(email1);
        String sanitizedEmail2 = sanitizeEmail(email2);

        return sanitizedEmail1.compareTo(sanitizedEmail2) < 0
                ? sanitizedEmail1 + "_" + sanitizedEmail2
                : sanitizedEmail2 + "_" + sanitizedEmail1;
    }

    private String sanitizeEmail(String email) {
        return email.replace("@", "_").replace(".", "_");
    }
}
