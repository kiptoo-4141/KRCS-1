package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.kenyaredcross.adapters.FeedbackAdapter;
import com.kenyaredcross.domain_model.Feedback;

import java.util.ArrayList;
import java.util.List;

public class ViewFeedbacksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private DatabaseReference feedbackRef;
    private EditText replyInput;
    private Button sendReplyButton;
    private final String currentUserEmail = "finance@gmail_com"; // Replace with actual logged-in user email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedbacks);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        replyInput = findViewById(R.id.replyInput);
        sendReplyButton = findViewById(R.id.sendReplyButton);

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList, currentUserEmail);
        recyclerView.setAdapter(feedbackAdapter);

        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback").child(currentUserEmail);

        loadFeedbacks();

        sendReplyButton.setOnClickListener(view -> sendReply());
    }

    private void loadFeedbacks() {
        feedbackRef.addValueEventListener(new ValueEventListener() {
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
        if (!replyText.isEmpty()) {
            String feedbackId = feedbackRef.push().getKey();
            Feedback reply = new Feedback(
                    feedbackId, "Reply to feedback", "2025-02-06", currentUserEmail, replyText,
                    feedbackId, "youth1@gmail_com", "Admin", "sent", "12:00:00"
            );
            if (feedbackId != null) {
                feedbackRef.child(feedbackId).setValue(reply);
                replyInput.setText("");
                Toast.makeText(this, "Reply sent", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
